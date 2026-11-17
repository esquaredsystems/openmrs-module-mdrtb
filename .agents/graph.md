# openmrs-module-mdrtb — Module Knowledge Graph
# Java OpenMRS module(Maven). Packaged as modules/mdrtb-x.y.z.omod. Requires OpenMRS ≥2.3.1.
# Shared facts (domain model, concept/encounter-type/identifier UUIDs, topology): ../.agents/graph.md
# Usage + notation legend + maintenance rules: ../.agents/instructions.md
# Last updated: 2026-11-17 (End of Treatment form fields + concepts 890-906, one form per program)

## § MODULE LAYOUT
```
pom.xml                             # parent Maven build (api + omod)
config.xml                          # OpenMRS module descriptor (id, version, required modules)
api/src/main/java/.../mdrtb/        # Domain logic: MdrtbConstants, MdrtbConcepts, patient/location models, services
omod/src/main/java/                 # Controllers + REST resource classes (ws endpoints under mdrtb/*)
omod/src/main/webapp/               # JSP views: chart, contacts, dashboard, drugforecast, form, program,
                                    #            regimen, reporting, specimen
omod/                               # builds the .omod artifact
commonlab_migrations.sql            # CommonLab schema migration helper
redeploy.bat                        # local Windows redeploy helper
changes.txt / instructions.txt      # historical notes
```

## § MAVEN DEPENDENCIES (compile-time)
```
mdrtb → openmrs-core 2.4.0
mdrtb → address-hierarchy 2.21.0
mdrtb → reporting 2.0.0
mdrtb → html-form-entry 2.1.0
mdrtb → webservices.rest 2.51.0
mdrtb → calculation 2.0.0
mdrtb → metadatamapping 1.7.0
mdrtb → idgen 5.0.4
mdrtb → cohort 3.7.3
mdrtb → legacyui 2.1.0          # aware-of only
```

## § REST RESOURCES EXPOSED (consumed by mdrtb-web at {base}/ws/rest/v1/…)
```
mdrtb/tb03 / mdrtb/tb03/{uuid}
mdrtb/tb03u / mdrtb/tb03u/{uuid}
mdrtb/form89 / mdrtb/form89/{uuid}   # Form 89; ?q={patientUuid}; POST needs patientProgramUuid
    # chain: Form89 (api form/custom) -> SimpleForm89 (dto) -> Form89ResourceController
    # save: does NOT use SimpleForm89.toForm() (dead code, as for every form here). It builds
    #       Form89 from the POSTed encounter's obs, so the DTO's flat fields are GET-only and
    #       writes happen purely through encounter.obs.
    # anatomicalSite is NOT a Form 89 obs: Form89.getAnatomicalSite() delegates to the linked
    #       TB03 form, unguarded, so it NPEs when tb03 is null. getByUniqueId calls initTB03
    #       first; doSearch does NOT, which is why ?q= would throw (web never calls it - the
    #       dashboard lists Form 89s through the generic /encounter endpoint instead).
    # methodOfDetection is MULTI-VALUED (2026-10-26): one obs per selected method.
    #       Form89.getMethodOfDetection() -> List<Concept> via TbUtil.getObsListFromEncounter;
    #       the setter reconciles (voids deselected, keeps obs of retained answers, adds new).
    #       processForm89 keeps otherMethodOfDetection when OTHER is AMONG the selected.
    #       Report PatientListContoller.getCasesByMethodOfDetectionTable lists a case under
    #       EVERY method it was detected by, so section counts no longer sum to the case total.
    # exposure/risk questions added 2026-10-26, each a single coded obs:
    #       contactWithTbPatient, laborMigration, afterReleaseFromPrison, riskyBehaviorFactors
    # EDIT CAVEAT: the web omits unanswered fields from the POST rather than voiding them, so
    #       clearing a field (or deselecting a method) leaves the previous obs live.
    # TESTS: Form89ResourceControllerTest + Form89DataResourceControllerTest are @Ignore stubs,
    #       and omod runs 0 tests anyway (see § TEST EXECUTION).
mdrtb/transferout / mdrtb/transferout/{uuid}
mdrtb/endoftreatment / mdrtb/endoftreatment/{uuid}   # End of Treatment form; ?q={patientUuid}; POST needs patientProgramUuid
    # Dispensary observation after treatment, ONE per patient program, shown on the program dashboard.
    # Source: End_of_Treatment_new.xlsx (ru/tj/en register columns), fields agreed 2026-11-17.
    # chain: EndOfTreatmentForm (api form/custom) -> SimpleEndOfTreatmentForm (dto) -> EndOfTreatmentFormResourceController
    #        legacy EndOfTreatmentFormController (controller/form, /module/mdrtb/form/endOfTreatment.form, NO JSP on purpose)
    # fields (GET): dispensaryGroup (Concept: Ia/Ib/Ic/Id), followupXrays [{monthOfTreatment, date, result}],
    #        followupLabTests [{monthOfTreatment, date, result, testNumber, lab}] READ ONLY,
    #        transferToGroup2Date, transferToGroup3Date, transferToGroup3Reason (Concept, ONE answer:
    #        MAJOR_RESIDUAL_CHANGES / DISABILITY / TB_HIV), removalFromObservationDate, removalFromObservationReason (text)
    #        Row values (Date/Concept) inside the lists are converted by ConversionUtil like top-level fields.
    # X-rays: NOT 5 fields - one obs group per month, NO limit on how many (agreed 2026-11-17):
    #        FOLLOWUP_XRAY_CONSTRUCT {MONTH_OF_TREATMENT, XRAY_DATE, FOLLOWUP_XRAY_RESULT}
    #        Concepts are reusable across encounter types; prefer an existing concept over creating a new one.
    #        result answers XRAY_NORMAL / XRAY_ABNORMAL / XRAY_INCONCLUSIVE. Changing one voids the old group.
    # lab tests: NOT stored. EndOfTreatmentForm.getFollowupSmears() = MdrtbService.getSmearForms(ppId) (COMMON TEST
    #        lab results + old specimen encounters) filtered by MONTH OF TREATMENT in FOLLOWUP_SMEAR_MONTHS {1,3,5},
    #        first per month (as TB03Util). date=encounterDatetime, testNumber=getSpecimenId(), lab=location name
    #        (same as the TB03 diagnostic smear columns).
    # concepts: 890-906 in the ETL concept CSVs (2026-11-17), see root graph. If one is missing in a database
    #        (ETL not run yet), its getter returns null/empty and its setter THROWS on any value (never stored
    #        under a wrong concept), and a POST with that obs fails. Reused: XRAY_DATE, DISABILITY.
    # save: MdrtbFormServiceImpl.processEndOfTreatmentForm rejects: no location / no program / program of another
    #       patient / no TB03 (DOTS) / no TB03u-MDR (MDR-TB) / a 2nd EoT on the same program (compared by encounter
    #       uuid, so editing passes) / X-ray month missing, negative or repeated / any form date in the future /
    #       X-ray date not AFTER end of treatment (= latest treatment outcome date on the program's TB03/TB03u;
    #       NOT checked, and form NOT refused, when no outcome date is recorded) / group III date not AFTER group II date /
    #       group III date without a group III reason.  "After" = Date.after (dates are entered without time, so the same day is refused).
    #       Clears the group III reason when there is no group III date. Removal reason is OPTIONAL.
    #       (rules agreed 2026-11-17)
    #       Has no outcome: does NOT touch program workflow or patient.dead.
    # getters: MdrtbService.getEndOfTreatmentFormsForProgram(patient, ppId), getEndOfTreatmentFormsFilled(locs, period)
    # report: see mdrtb/endoftreatmentreport below
    # OPEN: XDR (TB03u - XDR) accepted?
    #       month 3 and 5 smears are ALSO the TB03 treatment-period follow-up smears - same lab test shows in both.
    # TESTS (api, none @Ignore, written 2026-11-17 WITHOUT being run - no Maven here; first run on Uzair's machine):
    #        EndOfTreatmentFormTest (12: getters/setters, voiding, X-rays by month), EndOfTreatmentFormProcessTest (27: every
    #        save rule; seed in MdrtbTestService-initialData.xml: TB03/TB03u forms on HERMIONE 10003/10004/10005,
    #        harry keeps no obs - MdrtbPatientSummaryTest needs that), EndOfTreatmentDataTest (3), MdrtbServiceTest 2 smoke.
    #        omod EndOfTreatmentFormResourceControllerTest = dead (see § TEST EXECUTION)
    # TEST DATA: production concepts 890-906 are test ids 828-833 + 835-845 (UUIDs are what match, not ids). concept_numeric rows for 272 (MONTH OF TREATMENT)
    #        and 576 (PATIENT PROGRAM ID): OpenMRS 2.x ObsValidator NPEs on a numeric obs whose concept has no
    #        concept_numeric row. Encounter types TB03 (95), TB03u - MDR (96), End of Treatment (97) are in
    #        MdrtbTestService-initialData.xml and their mdrtb.encounterType.* GPs in global_property.xml, so
    #        MdrtbConstants.ET_TB03_TB_INTAKE / ET_TB03U_MDRTB_INTAKE / ET_END_OF_TREATMENT resolve in tests (they are
    #        static, resolved once per JVM at first use; encounter_type.xml is NOT in MdrtbTestBase.DATASETS).
    # !! TEST TRAP: MdrtbTestBase.initTestData() runs "CREATE TABLE IF NOT EXISTS", which COMMITS in H2, and the
    #        inherited test shouldInitiateTestData() calls it again. So put seed data in the shared datasets
    #        (MdrtbTestService-initialData.xml), not in an extra file loaded in @Before: that data would be committed
    #        and leak into later classes (seen 2026-11-17).
mdrtb/drugresistance / mdrtb/drugresistance/{uuid}
mdrtb/adverseevents / mdrtb/adverseevents/{uuid}
mdrtb/regimen / mdrtb/regimen/{uuid}
mdrtb/patientlist
mdrtb/tb03report / mdrtb/tb03ureport / mdrtb/form89report
mdrtb/endoftreatmentreport?type=dots|mdr&year=&quarter=|month=&location=   # End of Treatment report (register back page)
    # EndOfTreatmentData (api reporting/custom) -> SimpleEndOfTreatmentData (dto) -> EndOfTreatmentResourceController
    # patient set: EndOfTreatmentController (controller/reporting, static methods, NO request mappings/JSP):
    #   type=dots -> same patients as tb03report (getTB03FormsFilled), type=mdr -> same as tb03ureport
    #   (getTB03uFormsFilled); other/missing type -> 400 (IllegalRequestException), never a silent default.
    # each row joins the EoT form of the same patient program (getEndOfTreatmentFormsForProgram, first = only one);
    #   no EoT form -> endOfTreatmentUuid null and EoT columns empty. Labs come from the EoT form, so they
    #   are empty too until the form is saved (same as the form page).
    # followupXrays / followupLabTests = lists in one cell (ALL X-rays by month; smears of months 1,3,5)
    #   (agreed 2026-11-17, instead of the Excel's 5 X-ray + 2 lab columns). Dates as Context.getDateFormat(),
    #   concepts as names in Context.getLocale(), gender raw M/F (as TB03Data).
    # TESTS: api reporting/custom/EndOfTreatmentDataColumnsTest (5, DB); omod EndOfTreatmentResourceControllerTest
    #   = @Ignore placeholders (dead, see § TEST EXECUTION).
mdrtb/tb07report / mdrtb/tb07ureport / mdrtb/tb08report / mdrtb/tb08ureport
mdrtb/dataquality
mdrtb/yellowcard?formUuid={AE encounter uuid} | ?year=&quarter=|month=&location=   # Yellow Card (Annex 4), read only
    # YellowCardData (api reporting/pv) -> SimpleYellowCardData (dto) -> YellowCardDataResourceController
    # one card per filled causality drug (0 filled -> 1 card, causalityIndex=0)
    # RULE: only 1:1 recorded obs; checkboxes returned as CODES; no-box answers -> NOT_ON_CARD/OTHER + warnings[]
    # never filled: 6-14, 17, 19-29, 35, 43, 45-47, "reported first time"; SUSPECTED causality and
    # hospitalization (initial vs prolonged) tick nothing. Dates yyyy-MM-dd.
    # TESTS (no DB): api reporting/pv/YellowCardDataTest (68), omod web/dto/SimpleYellowCardDataTest (4).
    #   YellowCardData.Lookups is the seam: tests pass fakes instead of Context/MdrtbService.
```

## § TEST EXECUTION (measured 2026-10-26 — read before trusting "the tests pass")
```
mvn -o test  ->  api: 288 run, 0 failures, 16 skipped   |   omod: 0 run
omod runs NONE of its 32 compiled test classes:
  omod/pom.xml pins maven-surefire-plugin 2.9 (parent uses 2.22.2) and org.testng:testng:7.5
  is on the omod compile classpath, so surefire 2.9 picks the TestNG provider, which finds no
  JUnit tests. Proof: omod/target/surefire-reports/ holds TestNG's HTML assets
  ("Command line suite", bullet_point.png, collapseall.gif), not JUnit output.
  => a new omod test is dead on arrival; put module tests in api/ until this is fixed.
Formatter: net.revelc.code.formatter runs on every build and re-wraps long javadoc in files
  that were committed unformatted (currently the 4 EndOfTreatment* files), polluting diffs.
```

## § GLOBAL PROPERTIES (mdrtb.* namespace — stored in global_property table)
```
mdrtb.programName
mdrtb.dotsProgramName
mdrtb.primaryPatientIdentifierType
mdrtb.mdrtbPatientIdentifierType
mdrtb.dotsPatientIdentifierType
mdrtb.encounterType.tb03
mdrtb.encounterType.tb03u.mdr
mdrtb.encounterType.tb03u.xdr
mdrtb.encounterType.form89
mdrtb.encounterType.adverseEvent
mdrtb.encounterType.transferIn
mdrtb.encounterType.transferOut
mdrtb.encounterType.specimenCollection
mdrtb.encounterType.labResult
mdrtb.encounterType.resistanceDuringTreatment
mdrtb.encounterType.pvRegimen
mdrtb.encounterType.endOfTreatment   # default "End of Treatment" (MdrtbActivator) -> MdrtbConstants.ET_END_OF_TREATMENT
mdrtb.dst.formId
mdrtb.hain.formId
mdrtb.culture.formId
mdrtb.smear.formId
mdrtb.xpert.formId
labtest.mdrtbTestTypeUuid
```

## § LOCALIZATION
```
api/src/main/resources/messages.properties
api/src/main/resources/messages_{tj,ru,fr,id_ID}.properties
omod/src/main/resources/messages.properties
omod/src/main/resources/messages_{tj,ru,fr,id_ID}.properties
```

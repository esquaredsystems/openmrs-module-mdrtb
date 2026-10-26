# openmrs-module-mdrtb — Module Knowledge Graph
# Java OpenMRS module(Maven). Packaged as modules/mdrtb-x.y.z.omod. Requires OpenMRS ≥2.3.1.
# Shared facts (domain model, concept/encounter-type/identifier UUIDs, topology): ../.agents/graph.md
# Usage + notation legend + maintenance rules: ../.agents/instructions.md
# Last updated: 2026-10-26 (Form 89: multi-valued methodOfDetection + exposure/risk fields)

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
    # SCAFFOLD ONLY (2026-10-19): fields pending -> grep "TODO (EoT" for every place a field must be added
    # chain: EndOfTreatmentForm (api form/custom) -> SimpleEndOfTreatmentForm (dto) -> EndOfTreatmentFormResourceController
    #        legacy EndOfTreatmentFormController (controller/form, /module/mdrtb/form/endOfTreatment.form, NO JSP on purpose)
    # save: MdrtbFormServiceImpl.processEndOfTreatmentForm -> rejects if no location / no program / program of
    #       another patient / no TB03 (DOTS) / no TB03u-MDR (MDR-TB). Does NOT touch program workflow or patient.dead.
    # getters: MdrtbService.getEndOfTreatmentFormsForProgram(patient, ppId), getEndOfTreatmentFormsFilled(locs, period)
    # report: EndOfTreatmentData (api reporting/custom) exists; DTO + mdrtb/endoftreatmentreport resource NOT yet created
    # OPEN: XDR (TB03u - XDR) accepted?  duplicate EoT per program allowed?  outcome sync with TB03/TB03u?
    # TESTS: api EndOfTreatmentDataTest (runs), EndOfTreatmentFormProcessTest (@Ignore), MdrtbServiceTest 2 smoke;
    #        omod EndOfTreatmentFormResourceControllerTest (@Ignore)
mdrtb/drugresistance / mdrtb/drugresistance/{uuid}
mdrtb/adverseevents / mdrtb/adverseevents/{uuid}
mdrtb/regimen / mdrtb/regimen/{uuid}
mdrtb/patientlist
mdrtb/tb03report / mdrtb/tb03ureport / mdrtb/form89report
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

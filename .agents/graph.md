# openmrs-module-mdrtb — Module Knowledge Graph
# Java OpenMRS module v1.4.1 (Maven). Packaged as modules/mdrtb-1.4.1.omod. Requires OpenMRS ≥2.3.1.
# Shared facts (domain model, concept/encounter-type/identifier UUIDs, topology): ../.agents/graph.md
# Usage + notation legend + maintenance rules: ../.agents/instructions.md
# Last updated: 2026-07-02

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
mdrtb/form89 / mdrtb/form89/{uuid}
mdrtb/transferout / mdrtb/transferout/{uuid}
mdrtb/drugresistance / mdrtb/drugresistance/{uuid}
mdrtb/adverseevents / mdrtb/adverseevents/{uuid}
mdrtb/regimen / mdrtb/regimen/{uuid}
mdrtb/patientlist
mdrtb/tb03report / mdrtb/tb03ureport / mdrtb/form89report
mdrtb/tb07report / mdrtb/tb07ureport / mdrtb/tb08report / mdrtb/tb08ureport
mdrtb/dataquality
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

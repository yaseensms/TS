import static com.lumesse.devops.utils.testsSelCommon.testParameters
import static com.lumesse.devops.utils.testsSelCommon.authToken
import static com.lumesse.devops.utils.testsSelCommon.mainFolder
import com.lumesse.devops.utils.CommonUtils

testParameters.each{ folders, tests ->
  tests.each{ testType, testSettings ->

    def fPath = [mainFolder, folders]
    fPath.size().times {
      folder( fPath.take(it+1).join("/") ){
        configure {
          folder_properties = it.get('properties').get(0)
          auth_matrix_prop = folder_properties.appendNode(
                  'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'
          )
          auth_matrix_prop.appendNode('permission').setValue(
                  "hudson.model.Item.Create:kchlopek"
          )
          auth_matrix_prop.appendNode('permission').setValue(
                  "hudson.model.Item.Delete:kchlopek"
          )
          auth_matrix_prop.appendNode('permission').setValue(
                  "hudson.model.Item.Configure:kchlopek"
          )
          auth_matrix_prop.appendNode('permission').setValue(
                  "hudson.model.Item.Cancel:kchlopek"
          )
          auth_matrix_prop.appendNode('permission').setValue(
                  "hudson.model.Item.Build:kchlopek"
          )
        }
      }
    }
    String jenkinsUrl = binding.variables.get("JENKINS_URL")
    def jobTest
    if (folders != 'other') {
      jobTest = "${testType}-${folders}"
    }
    else {
      jobTest = "${testType}"
    }
    job( (fPath << jobTest).join("/")) {

      authenticationToken("${authToken}")

      label('selenium')
      if (testParameters[folders][testType]['TEST_ACCESSIBLE'] == 'Y') {
        description("""
        |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${testType}/ws/target/surefire-reports/html/index.html\">Test Execution Report</a></br>
        |
        |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${testType}/ws/target/surefire-reports/html/MissingTranslation.html\">MissingTranslation.html</a></br>
        |
        |<ul><strong>Meaning of build colors:</strong>
        |<li><img src="/static/67a99fe2/images/16x16/blue.png"> - all tests were passed.</li>
        |<li><img src="/static/67a99fe2/images/16x16/yellow.png"> - some tests were failed but after re-run all of them passed.</li>
        |<li><img src="/static/67a99fe2/images/16x16/red.png"> - there are some tests that are still failing.</li>
        |</ul>
        |
        |<br><strong>DO NOT</strong> edit this job in Jenkins.
        |
        |Please issue a Pull Request with your changes.</br>
        """.stripMargin().replaceAll(System.getProperty("line.separator"), '<br />'))
      } else {
        if (testType =~ 'smoke') {
          description("""
          |Smoke suite collect the most important tests to verify if TLK doesn't have any critical problems after build on ${folders}.
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${testType}-${folders}/ws/target/surefire-reports/html/index.html\">Test Execution Report</a></br>
          |
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${testType}-${folders}/ws/target/surefire-reports/html/MissingTranslation.html\">MissingTranslation.html</a></br>
          |
          |<ul><strong>Meaning of build colors:</strong>
          |<li><img src="/static/67a99fe2/images/16x16/blue.png"> - all tests were passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/yellow.png"> - some tests were failed but after re-run all of them passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/red.png"> - there are some tests that are still failing.</li>
          |</ul>
          |
          |<br><strong>DO NOT</strong> edit this job in Jenkins.
          |
          |Please issue a Pull Request with your changes.</br>
          """.stripMargin().replaceAll(System.getProperty("line.separator"), '<br />'))
        } else {
          if (folders == 'tlk-axe') {
            description("""
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${jobTest}/ws/target/surefire-reports/html/index.html\">Test Execution Report</a></br>
          |
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${jobTest}/ws/target/surefire-reports/html/global_report.html\">Axe Global Report</a></br>
          |
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${jobTest}/ws/target/surefire-reports/html/MissingTranslation.html\">MissingTranslation.html</a></br>
          |
          |<ul><strong>Meaning of build colors:</strong>
          |<li><img src="/static/67a99fe2/images/16x16/blue.png"> - all tests were passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/yellow.png"> - some tests were failed but after re-run all of them passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/red.png"> - there are some tests that are still failing.</li>
          |</ul>
          |
          |<br><strong>DO NOT</strong> edit this job in Jenkins.
          |
          |Please issue a Pull Request with your changes.</br>
          """.stripMargin().replaceAll(System.getProperty("line.separator"), '<br />'))
          } else {
            description("""
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${jobTest}/ws/target/surefire-reports/html/index.html\">Test Execution Report</a></br>
          |
          |<br><a href=\"/job/${mainFolder}/job/${folders}/job/${jobTest}/ws/target/surefire-reports/html/MissingTranslation.html\">MissingTranslation.html</a></br>
          |
          |<ul><strong>Meaning of build colors:</strong>
          |<li><img src="/static/67a99fe2/images/16x16/blue.png"> - all tests were passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/yellow.png"> - some tests were failed but after re-run all of them passed.</li>
          |<li><img src="/static/67a99fe2/images/16x16/red.png"> - there are some tests that are still failing.</li>
          |</ul>
          |
          |<br><strong>DO NOT</strong> edit this job in Jenkins.
          |
          |Please issue a Pull Request with your changes.</br>
          """.stripMargin().replaceAll(System.getProperty("line.separator"), '<br />'))
          }
        }
      }

      wrappers {
        preBuildCleanup()
        if (testParameters[folders][testType]['SAVE_RESULTS_IN_DB'] == 'Y') {
          credentialsBinding {
            usernamePassword('IGNORED_USER_NAME', 'DB_PASSWORD', 'selenium-db-password')
          }
        }
      } //end wrappers

      jdk('java11_openjdk')

      parameters {
        stringParam('BRANCH', testParameters[folders][testType]['BRANCH'] as String, 'branch / sha name that should be taken to build package')
        choiceParam('CONFIG', testParameters[folders][testType]['ENV_PARAM'] as List<String>)

        if (testParameters[folders][testType].containsKey('GROUP')) {
          choiceParam('GROUP', testParameters[folders][testType]['GROUP'] as List<String>)
        }
        if (testParameters[folders][testType].containsKey('TEST')) {
          stringParam('TEST', testParameters[folders][testType]['TEST'] as String)
        }
        if (testParameters[folders][testType].containsKey('AREA')) {
          choiceParam('AREA', testParameters[folders][testType]['AREA'] as List<String>)
        }

        choiceParam('GRID', testParameters[folders][testType]['GRID'] as List<String>)
        choiceParam('HEADLESS', testParameters[folders][testType]['HEADLESS'] as List<String>)
        choiceParam('MISSING_TRANSLATION', testParameters[folders][testType]['MISSING_TRANSLATION'] as List<String>)

        if (testParameters[folders][testType].containsKey('MOBILE_DEVICE')) {
          choiceParam('MOBILE_DEVICE', testParameters[folders][testType]['MOBILE_DEVICE'] as List<String>)
        }
        if (testParameters[folders][testType].containsKey('TRIGGERED_JOB_NAME')) {
          choiceParam('TRIGGERED_JOB_NAME', testParameters[folders][testType]['TRIGGERED_JOB_NAME'])
          stringParam('LAST_BUILD_NUMBER', 'recent')
        }
      }  //end parameters

      configure {
        it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
          'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
            delegate.name('POOL')
            delegate.defaultValue(testParameters[folders][testType]['POOL'])
            delegate.description('Hidden parameter')
          }
        }
        it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
          'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
            delegate.name('THREAD')
            delegate.defaultValue(testParameters[folders][testType]['THREAD'])
            delegate.description('Hidden parameter')
          }
        }
        if (testParameters[folders][testType].containsKey('PIPELINE_RUN')) {
          it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
            'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
              delegate.name('PIPELINE_RUN')
              delegate.defaultValue(testParameters[folders][testType]['PIPELINE_RUN'])
              delegate.description('Hidden parameter')
            }
          }
        }
        if (testParameters[folders][testType]['SAVE_RESULTS_IN_DB'] == 'Y') {
          it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
            'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
              delegate.name('DB_URL')
              delegate.defaultValue('jdbc:postgresql://ff-ta-dev-shared-shareddb-postgres.c3xia2rqebfe.eu-central-1.rds.amazonaws.com:5432/selenium?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory')
              delegate.description('Hidden parameter')
            }
          }
          it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
            'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
              delegate.name('DB_USERNAME')
              delegate.defaultValue('selenium')
              delegate.description('Hidden parameter')
            }
          }
        }
        if (testParameters[folders][testType]['RESULT_DASHBOARD'] == 'Y') {
          it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
            'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
              delegate.name('RESULT_DASHBOARD')
              delegate.defaultValue('true')
              delegate.description('Hidden parameter')
            }
          }
        }
        if (testParameters[folders][testType].containsKey('REPOSITORY')) {
          it / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions' << {
            'com.wangyin.parameter.WHideParameterDefinition' {  //hidden parameter
              delegate.name('REPOSITORY')
              delegate.defaultValue(testParameters[folders][testType]['REPOSITORY'])
              delegate.description('Hidden parameter')
            }
          }
        }
      } //end configure

      authorization {
        permission('hudson.model.Item.Configure', testParameters[folders][testType]['PERMISSION'][0])
        testParameters[folders][testType]['PERMISSION'].each { permissionGroup ->
          permission('hudson.model.Item.Build', permissionGroup)
          permission('hudson.model.Item.Cancel', permissionGroup)
          permission('hudson.model.Item.Workspace', permissionGroup)
        }
      }  //end authorization

      logRotator {
        numToKeep(30)
        daysToKeep(20)
      } //end logRotator

      steps {
        if (testParameters[folders][testType]['CLEAN_GRID_AND_APPLY'] == 'Y') {
          shell("""#!/bin/bash +x
                curl -s -k https://batch-dev-tlk.lumesse.top/batch/republish-apply-configs.cfm?nAccountId=""".stripMargin().concat(testParameters[folders][testType]['SLICE_NUMBER'])
                  .stripMargin()) // end shell script
        }

        if (testParameters[folders][testType].containsKey('TRIGGERED_JOB_NAME')) {
          if (testType =~ 'with-rerun') {
            downstreamParameterized{
              trigger(testParameters[folders][testType]['TRIGGERED_JOB_NAME']) {
                block {
                  buildStepFailure('ABORTED')
                  failure('ABORTED')
                  unstable('ABORTED')
                }
                parameters {
                  predefinedProp('CONFIG', '\${CONFIG}')
                  predefinedProp('GROUP', '\${GROUP}')
                  predefinedProp('AREA', '\${AREA}')
                  predefinedProp('VERSION', '\${VERSION}')
                  predefinedProp('BRANCH', '\${BRANCH}')
                  predefinedProp('GRID', '\${GRID}')
                  predefinedProp('UI', '\${UI}')
                  predefinedProp('POOL', '\${POOL}')
                  predefinedProp('THREAD', '\${THREAD}')
                  predefinedProp('HEADLESS', '\${HEADLESS}')
                  predefinedProp('MISSING_TRANSLATION', '\${MISSING_TRANSLATION}')
                  predefinedProp('PIPELINE_RUN', '\${PIPELINE_RUN}')
                  if (testParameters[folders][testType]['MOBILE_DEVICE_VISIBILITY'] == 'Y') {
                    predefinedProp('MOBILE_DEVICE', '\${MOBILE_DEVICE}')
                  }
                  if (testParameters[folders][testType]['RESULT_DASHBOARD'] == 'Y') {
                    predefinedProp('RESULT_DASHBOARD', '\${RESULT_DASHBOARD}')
                  }
                  if (testParameters[folders][testType]['SAVE_RESULTS_IN_DB'] == 'Y') {
                    predefinedProp('DB_URL', '\${DB_URL}')
                    predefinedProp('DB_USERNAME', '\${DB_USERNAME}')
                    predefinedProp('DB_PASSWORD', '\${DB_PASSWORD}')
                  }
                }
              }
            }
          }

          shell("""#!/bin/bash +x
          |if [ "\${LAST_BUILD_NUMBER}" = "recent" ]
          |then
          |RESULT=`curl -s -L ${jenkinsUrl}/job/${mainFolder}/job/${folders}/job/\${TRIGGERED_JOB_NAME}/lastBuild/api/json | jq -r ".result"`
          |else
          |RESULT=`curl -s -L ${jenkinsUrl}/job/${mainFolder}/job/${folders}/job/\${TRIGGERED_JOB_NAME}/\${LAST_BUILD_NUMBER}/api/json | jq -r ".result"`
          |fi
          |if [ "\${RESULT}" = "SUCCESS" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |elif [ "\${RESULT}" = "FAILURE" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |elif [ "\${RESULT}" = "UNSTABLE" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |fi
          """.stripMargin())

          environmentVariables {
            propertiesFile("export_props.properties")
          }

          conditionalSteps{
            condition {
              stringsMatch('\${RESULT_VALUE}', 'FAILURE', false)
            }
            runner('Unstable')

            steps {
              setBuildResult('UNSTABLE')
              shell("""#!/bin/bash +x
              |git clone ssh://git@git.lumesse.top/tlk/integration-tests.git
              |cd integration-tests
              |git checkout \${BRANCH}
              """.stripMargin())
            }
          }

          conditionalSteps{
            condition {
              and
                      {stringsMatch('\${LAST_BUILD_NUMBER}', 'recent', false)}
                      {stringsMatch('\${RESULT_VALUE}', 'FAILURE', false)}
            }
            steps{
              copyArtifacts('\${TRIGGERED_JOB_NAME}') {
                includePatterns('xmlsuites/**/*.*')
                targetDirectory('\${WORKSPACE}/integration-tests/integration-tests-ta/')
                flatten(false)
                optional(false)
                buildSelector {
                  workspace()
                }
              }
            }
          }

          conditionalSteps{
            condition {
              and
                      {not {stringsMatch('\${LAST_BUILD_NUMBER}', 'recent', false)}}
                      {stringsMatch('\${RESULT_VALUE}', 'FAILURE', false)}

            }
            steps{
              copyArtifacts('\${TRIGGERED_JOB_NAME}') {
                includePatterns('xmlsuites/**/*.*')
                targetDirectory('\${WORKSPACE}/integration-tests/integration-tests-ta/')
                flatten(false)
                optional(false)
                buildSelector {
                  buildNumber('\${LAST_BUILD_NUMBER}')
                }
              }
            }
          }

          conditionalSteps {
            condition {
              and
                      {status('FAILURE', 'UNSTABLE')}
                      {fileExists('integration-tests/integration-tests-ta/xmlsuites/',BaseDir.WORKSPACE)}
            }
            steps{
              shell("""#!/bin/bash +x
              |cd \${WORKSPACE}/integration-tests/integration-tests-ta/xmlsuites/
              |files=\$(ls | grep \\.xml\$)
              |for i in \$files; do
              |  files2=\${i%%.*}
              |  var+=\$(printf "%s," \${files2[@]})
              |done
              |echo VALUE=\${var::-1} > \${WORKSPACE}/failedSuites_props.properties
              """.stripMargin())
              environmentVariables {
                propertiesFile('\${WORKSPACE}/failedSuites_props.properties')
              }
              shell("""#!/bin/bash +x
              |cd integration-tests
              |mvn versions:set -DnewVersion="rerun"
              |mvn clean install -DskipTests -U
              """.stripMargin())
            }
          }

          conditionalSteps {
            condition {
              and
                      {status('FAILURE', 'UNSTABLE')}
                      {filesMatch(includes='integration-tests/integration-tests-ta/xmlsuites/*.xml', excludes = '', BaseDir.WORKSPACE)}
            }
            steps {
              shell(shellRunTests(testParameters[folders][testType]))
            }
          }

          shell("""#!/bin/bash
          |if [ "\${LAST_BUILD_NUMBER}" = "recent" ]
          |then
          |RESULT=`curl -s -L ${jenkinsUrl}/job/${mainFolder}/job/${folders}/job/\${TRIGGERED_JOB_NAME}/lastBuild/api/json | jq -r ".result"`
          |else
          |RESULT=`curl -s -L ${jenkinsUrl}/job/${mainFolder}/job/${folders}/job/\${TRIGGERED_JOB_NAME}/\${LAST_BUILD_NUMBER}/api/json | jq -r ".result"`
          |fi
          |if [ "\${RESULT}" = "SUCCESS" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |elif [ "\${RESULT}" = "FAILURE" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |elif [ "\${RESULT}" = "UNSTABLE" ]
          |then
          |echo RESULT_VALUE = "\${RESULT}" > export_props.properties
          |fi
          """.stripMargin())

          environmentVariables {
            propertiesFile("export_props.properties")
          }

          conditionalSteps{
            condition {
              stringsMatch('\${RESULT_VALUE}', 'FAILURE', false)
            }
            runner('Run')

            steps {
              setBuildResult('UNSTABLE')
            }
          }
        } else {
          downstreamParameterized {
            trigger("../builders/test-jar-builder") {
              block {
                buildStepFailure('FAILURE')
                failure('FAILURE')
                unstable('UNSTABLE')
              }
              parameters {
                predefinedProp('BRANCH', '\${BRANCH}')
              }
            }
          } // end downstreamParameterized

          shell(shellRunTests(testParameters[folders][testType]))
        }

        publishers{
          publishBuild {
            discardOldBuilds(20, 20)
          }
          archiveArtifacts('target/**/*.*, test-output/**/*.*, xmlsuites/**/*.*')
          archiveTestNG('test-output/testng-results.xml') {
            escapeTestDescription(true)
            escapeExceptionMessages(true)
            showFailedBuildsInTrendGraph(true)
            markBuildAsUnstableOnSkippedTests(true)
            markBuildAsFailureOnFailedConfiguration(true)
          }

          postBuildTask {
            task('Test execution finished. Have a nice day!', '''
            perl -pi.bak -e "s;href=\';href=\'${JOB_URL}/${BUILD_NUMBER}/artifact/target/surefire-reports/html/;g" ${WORKSPACE}/test-output/testng-results.xml
            perl -pi.bak -e "s;src=\';src=\'${JOB_URL}/${BUILD_NUMBER}/artifact/target/surefire-reports/html/;g" ${WORKSPACE}/test-output/testng-results.xml
            ''')
          }

          if (folders == 'tlk-axe') {
            buildDescription('', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/index.html">Selenium Report</a><br><a href="\${BUILD_URL}artifact/target/surefire-reports/html/global_report.html">Axe Report</a>',
                    '', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/index.html">Selenium Report</a><br><a href="\${BUILD_URL}artifact/target/surefire-reports/html/global_report.html">Axe Report</a>')
          } else if (testParameters[folders][testType].containsKey('TEST')) {
            buildDescription('', '<a href="${BUILD_URL}artifact/target/surefire-reports/html/index.html">Report from test ${TEST} on ${CONFIG} from branch ${BRANCH}</a>',
                    '', '<a href="${BUILD_URL}artifact/target/surefire-reports/html/index.html">Report from test ${TEST} on ${CONFIG} from branch ${BRANCH}</a>')
          } else {
            buildDescription('', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/index.html">Report</a>',
                    '', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/index.html">Report</a>')
          }

          flexiblePublish {
            conditionalAction {
			  condition {
				status('SUCCESS', 'SUCCESS')
              }
              steps {
                buildDescription('', 'No skipped and failed tests to re-run.')
              }
            }
            conditionalAction {
              condition {
                stringsMatch('\${MISSING_TRANSLATION}', 'true', false)
              }
              publishers {
                buildDescription('', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/MissingTranslation.html">MissingTranslation.html</a>',
                        '', '<a href="\${BUILD_URL}artifact/target/surefire-reports/html/MissingTranslation.html">MissingTranslation.html</a>')
              }
            }
          } //end flexiblePublish

          if (testParameters[folders][testType]['RESULT_DASHBOARD'] == 'Y') {
            downstreamParameterized {
              trigger('../result-dashboard/file-uploader-result-dashboard') {
                condition("ALWAYS")
                parameters {
                  predefinedProp('BRANCH', '\${BRANCH}')
                  predefinedProp('JOB_NAME', '../' + folders + '/' + testType + '-' + folders)
                }
              }
            }
          }

        }  //end publishers
      }  //end steps

      if (CommonUtils.isNewJenkins(binding)) {
        triggers {
          def cronSchedule = "${testParameters[folders][testType]['CRON']?.join('\n')}"
          cron(cronSchedule)
        }
      } //end triggers
    } //end job
  } //end tests.each
} //end testParameters.each

static String shellRunTests(testParameters) {
  """#!/bin/bash +x
        java -Ddriver=""".stripMargin().concat("""\${GRID}""").stripMargin().concat(""" \\
        -Dconfig=""".stripMargin().concat("""\${CONFIG} """).stripMargin().concat(
          testParameters.containsKey('RESULT_DASHBOARD') ? "-DresultDashboard=\${RESULT_DASHBOARD} " : "").stripMargin().concat(
          testParameters.containsKey('TRIGGERED_JOB_NAME') ? "-Dsuite=\${VALUE} " : (
          testParameters.containsKey('GROUP') ? "-Dgroup=\${GROUP} " : "").stripMargin().concat(
          testParameters.containsKey('AREA') ? "-Darea=\${AREA} " : "").stripMargin().concat(
          testParameters.containsKey('TEST') ? "-Dtest=\${TEST} " : "")).stripMargin().concat(
          testParameters.containsKey('MOBILE_DEVICE') ? "-DmobileDevice=\${MOBILE_DEVICE} " : "").stripMargin().concat(
          testParameters.containsKey('SAVE_RESULTS_IN_DB') ? "-DdbUrl=\${DB_URL} -DdbUsername=\${DB_USERNAME} -DdbPassword=\${DB_PASSWORD} " : "").stripMargin().concat(
          testParameters.containsKey('PIPELINE_RUN') ? "-DpipelineRun=\${PIPELINE_RUN} " : "-DpipelineRun=false ").stripMargin().concat(""" \\
        -DsuiteThreadPoolSize=""".stripMargin().concat("""\${POOL}""").stripMargin().concat(""" \\
        -DthreadCount=""".stripMargin().concat("""\${THREAD}""").stripMargin().concat(""" \\
        -Dheadless=""".stripMargin().concat("""\${HEADLESS}""").stripMargin().concat(""" \\
        -DmissingTranslation=""".stripMargin().concat("""-\${MISSING_TRANSLATION}""").stripMargin().concat(""" \\
        -Dfile.encoding=utf-8 """.stripMargin().concat(
          testParameters.containsKey('TRIGGERED_JOB_NAME') ? "-jar integration-tests/integration-tests-assembly/target/integration-tests-assembly-rerun.jar \\" : "-jar /mnt/packages/integration-tests-assembly-\${BRANCH}.jar \\").stripMargin()))))))
}

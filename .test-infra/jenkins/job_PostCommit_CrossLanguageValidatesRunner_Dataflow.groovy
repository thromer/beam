/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import CommonJobProperties as commonJobProperties
import PostcommitJobBuilder

import static PythonTestProperties.CROSS_LANGUAGE_VALIDATES_RUNNER_DATAFLOW_USING_JAVA_PYTHON_VERSIONS
import static PythonTestProperties.CROSS_LANGUAGE_VALIDATES_RUNNER_DATAFLOW_USING_SQL_PYTHON_VERSIONS

// This job runs the suite of ValidatesRunner tests against the Dataflow runner.
PostcommitJobBuilder.postCommitJob('beam_PostCommit_XVR_Dataflow',
    'Run XVR_Dataflow PostCommit', 'Dataflow CrossLanguageValidatesRunner Tests', this) {
      description('Runs the CrossLanguageValidatesRunner suite on the Dataflow runner.')

      // Set common parameters.
      commonJobProperties.setTopLevelMainJobProperties(delegate)

      // Publish all test results to Jenkins
      publishers {
        archiveJunit('**/nosetests*.xml')
      }

      // Gradle goals for this job.
      // TODO: We only support Python pipeline using Java external transforms at the moment.
      // Enable testing for Java pipeline using Python external transforms when BEAM-11203
      // is implemented.
      steps {
        CROSS_LANGUAGE_VALIDATES_RUNNER_DATAFLOW_USING_JAVA_PYTHON_VERSIONS.each { pythonVersion ->
          shell("echo \"*** RUN CROSS-LANGUAGE DATAFLOW PYTHON WITH JAVA EXTERNAL TRANSFORMS USING PYTHON ${pythonVersion} ***\"")
          gradle {
            rootBuildScriptDir(commonJobProperties.checkoutDir)
            tasks(':runners:google-cloud-dataflow-java:validatesCrossLanguageRunnerPythonUsingJava')
            commonJobProperties.setGradleSwitches(delegate)
            switches("-PpythonVersion=${pythonVersion}")
          }
        }
        CROSS_LANGUAGE_VALIDATES_RUNNER_DATAFLOW_USING_SQL_PYTHON_VERSIONS.each { pythonVersion ->
          shell("echo \"*** RUN CROSS-LANGUAGE DATAFLOW PYTHON WITH JAVA SQL TRANSFORMS USING PYTHON ${pythonVersion} ***\"")
          gradle {
            rootBuildScriptDir(commonJobProperties.checkoutDir)
            tasks(':runners:google-cloud-dataflow-java:validatesCrossLanguageRunnerPythonUsingSql')
            commonJobProperties.setGradleSwitches(delegate)
            switches("-PpythonVersion=${pythonVersion}")
          }
        }
      }
    }

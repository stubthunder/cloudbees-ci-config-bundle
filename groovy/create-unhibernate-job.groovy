import jenkins.model.*;
import java.util.logging.Logger;

Logger logger = Logger.getLogger("create-unhibernate-job.groovy");

def jenkins = Jenkins.instance
def name = "unhibernate"

def job = jenkins.getItem(name)
if (job != null) {
  logger.info("job $name already existed so deleting")
  job.delete()
}

//11-12 UTC Monday thru Friday, update for your timezone
//this would be 6-7am EST M-F
def cronSchedule = "H 11 * * 1-5"

//unhibernate job
def unhibernateJobXml = """
<flow-definition plugin="workflow-job@2.40">
  <actions>
    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin="pipeline-model-definition@1.7.2"/>
    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin="pipeline-model-definition@1.7.2">
      <jobProperties/>
      <triggers/>
      <parameters/>
      <options/>
    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>
  </actions>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <jenkins.model.BuildDiscarderProperty>
      <strategy class="hudson.tasks.LogRotator">
        <daysToKeep>-1</daysToKeep>
        <numToKeep>1</numToKeep>
        <artifactDaysToKeep>-1</artifactDaysToKeep>
        <artifactNumToKeep>-1</artifactNumToKeep>
      </strategy>
    </jenkins.model.BuildDiscarderProperty>
    <org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
      <triggers>
        <hudson.triggers.TimerTrigger>
          <spec>${cronSchedule}</spec>
        </hudson.triggers.TimerTrigger>
      </triggers>
    </org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
  </properties>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition" plugin="workflow-cps@2.83">
    <script>pipeline {
    agent none
    stages {
        stage(&apos;Wake Up!&apos;) {
            steps {
                echo &quot;I&apos;m awake!&quot;
            }
        }
    }
}</script>
    <sandbox>true</sandbox>
  </definition>
  <triggers/>
  <disabled>false</disabled>
</flow-definition>
"""

def p = jenkins.createProjectFromXML(name, new ByteArrayInputStream(unhibernateJobXml.getBytes("UTF-8")));

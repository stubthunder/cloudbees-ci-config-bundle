import jenkins.model.*;
import org.jenkinsci.plugins.workflow.libs.*;
import jenkins.scm.api.SCMSource;
import jenkins.plugins.git.*; 
import com.cloudbees.pipeline.governance.templates.*;
import com.cloudbees.pipeline.governance.templates.catalog.*;
import org.jenkinsci.plugins.github.GitHubPlugin;
import java.util.logging.Logger;

Logger logger = Logger.getLogger("create-pipeline-template-catalog.groovy");

def jenkins = Jenkins.instance
def name = "microblog-frontend"
def microBlogJob = jenkins.getItemByFullName(name)
if (microBlogJob == null) {
  //Pipeline Template Catalog
  SCMSource scm = new GitSCMSource("https://github.com/REPLACE_GITHUB_ORG/pipeline-template-catalog.git");
  scm.setCredentialsId("github-sa");
  TemplateCatalog catalog = new TemplateCatalog(scm, "master");
  catalog.setUpdateInterval("1h");
  GlobalTemplateCatalogManagement.get().addCatalog(catalog);
  GlobalTemplateCatalogManagement.get().save();
  logger.info("Creating new Pipeline Template Catalog");
  catalog.updateFromSCM(); 

  //microblog-fronted job from Pipeline Template
  def frontendJobXml = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.21">
    <properties>
      <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.7">
        <instance>
          <model>cb-demo/vuejs-app</model>
          <values class="tree-map">
            <entry>
              <string>gcpProject</string>
              <string>core-flow-research</string>
            </entry>
            <entry>
              <string>githubCredentialId</string>
              <string>github-sa</string>
            </entry>
            <entry>
              <string>name</string>
              <string>microblog-frontend</string>
            </entry>
            <entry>
              <string>registry</string>
              <string>gcr.io/core-flow-research</string>
            </entry>
            <entry>
              <string>repoOwner</string>
              <string>REPLACE_GITHUB_ORG</string>
            </entry>
            <entry>
              <string>repository</string>
              <string>microblog-frontend</string>
            </entry>
          </values>
        </instance>
      </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
    </properties>
    <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.7">
      <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.."/>
      <catalogName>cb-demo</catalogName>
      <templateDirectory>vuejs-app</templateDirectory>
    </factory>
  </org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def p = jenkins.createProjectFromXML(name, new ByteArrayInputStream(frontendJobXml.getBytes("UTF-8")));

  logger.info("created $name job")

 def frontendInsuranceXML = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.21">
    <properties>
      <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.7">
        <instance>
        <model>cb-demo/react-app</model>
        <values class="tree-map">
          <entry>
            <string>gcpProject</string>
            <string>core-flow-research</string>
          </entry>
          <entry>
            <string>githubCredentialId</string>
            <string>github-sa</string>
          </entry>
          <entry>
            <string>name</string>
            <string>Insurance Frontend</string>
          </entry>
          <entry>
            <string>repoOwner</string>
            <string>REPLACE_GITHUB_ORG</string>
          </entry>
          <entry>
            <string>repository</string>
            <string>insurance-frontend</string>
          </entry>
        </values>
      </instance>
      </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
    </properties>
    <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.7">
      <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.."/>
      <catalogName>cb-demo</catalogName>
      <templateDirectory>react-app</templateDirectory>
    </factory>
  </org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def insuranceFrontend = jenkins.createProjectFromXML("Insurance Frontend", new ByteArrayInputStream(frontendInsuranceXML.getBytes("UTF-8")));

  def backendName = "microblog-backend"
  //microblog-backend job from Pipeline Template
  def backendJobXml = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.21">
    <properties>
      <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.7">
        <instance>
          <model>cb-demo/python</model>
          <values class="tree-map">
            <entry>
              <string>gcpProject</string>
              <string>core-flow-research</string>
            </entry>
            <entry>
              <string>githubCredentialId</string>
              <string>github-sa</string>
            </entry>
            <entry>
              <string>name</string>
              <string>microblog-backend</string>
            </entry>
            <entry>
              <string>repoOwner</string>
              <string>REPLACE_GITHUB_ORG</string>
            </entry>
            <entry>
              <string>repository</string>
              <string>microblog-backend</string>
            </entry>
          </values>
        </instance>
      </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
    </properties>
    <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.7">
      <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.."/>
      <catalogName>cb-demo</catalogName>
      <templateDirectory>python</templateDirectory>
    </factory>
  </org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def backendProject = jenkins.createProjectFromXML(backendName, new ByteArrayInputStream(backendJobXml.getBytes("UTF-8")));

  logger.info("created $backendName job")  

  def insuranceBackendXML = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.26">
  <properties>
    <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.12">
      <instance>
        <model>cb-demo/python-poetry</model>
        <values class="tree-map">
          <entry>
            <string>gcpProject</string>
            <string>core-flow-research</string>
          </entry>
          <entry>
            <string>githubCredentialId</string>
            <string>github-sa</string>
          </entry>
          <entry>
            <string>name</string>
            <string>Insurance Backend</string>
          </entry>
          <entry>
            <string>repoOwner</string>
            <string>REPLACE_GITHUB_ORG</string>
          </entry>
          <entry>
            <string>repository</string>
            <string>insurance-backend</string>
          </entry>
        </values>
      </instance>
    </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
  </properties>
  <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.12">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
    <catalogName>cb-demo</catalogName>
    <templateDirectory>python-poetry</templateDirectory>
  </factory>
</org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def insuranceBackend = jenkins.createProjectFromXML("Insurance Backend", new ByteArrayInputStream(insuranceBackendXML.getBytes("UTF-8")));

  
} else {
  logger.info("$name job already exists")
}

import hudson.model.*
import jenkins.model.*
import javaposse.jobdsl.plugin.*

//Get instance of Jenkins
def parent = Jenkins.getInstance()

//Define a job name
def jobName = "seedjob"

//Instantiate a new project
def project = new FreeStyleProject(parent, jobName);
def jobDslBuildStep = new ExecuteDslScripts(
'''
buildFlowJob('trigger') {
  concurrentBuild()
  buildFlow(
  """
import hudson.model.CheckPoint
final String sessionId = "session-" + build.number
Random random = new Random()

build1 = build( "step-1", RANDOM: random.nextInt(30) )
build1.setDescription(sessionId)

build2 = build( "step-2", RANDOM: random.nextInt(30) )
build2.setDescription(sessionId)

build3 = build( "step-3", RANDOM: random.nextInt(30) )
build3.setDescription(sessionId)

build4 = build( "step-4", RANDOM: random.nextInt(30) )
build4.setDescription(sessionId)

final String checkPointName = "ReleaseSync"
final CheckPoint releaseCP = new CheckPoint(checkPointName)
out.println('Waiting for previous builds to finish')
releaseCP.block()

build5 = build( "step-5", RANDOM: random.nextInt(30) )
build5.setDescription(sessionId)
releaseCP.report() 
""")
}

job('step-1') {
  concurrentBuild()
  steps {
    shell('sleep $RANDOM')
  }
}

job('step-2') {
  using('step-1')
}

job('step-3') {
  using('step-1')
}

job('step-4') {
  using('step-1')
}

job('step-5') {
  using('step-1') }

''');

project.getBuildersList().add(jobDslBuildStep);

project.save()
parent.reload()

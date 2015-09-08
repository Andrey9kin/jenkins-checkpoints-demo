# Synchronizing parallel delivery flows in Jenkins using Groovy, BuildFlow and a bit of black magic

## What is included

* Dockerfile - Thin docker build file. All magic is done in [Jenkins official build file](https://github.com/jenkinsci/docker/tree/9395d3fdd74cd43f03b1844fbb0c3e48d713cbc1). I'm only providing list of plugins to install and few groovy scripts for initial configuration 
* plugins.txt - list of plugins that will be automatically installed. All versions of the plugins fixed as well as Jenkins version in order to ensure reproducibility of the setup as time goes.
  * [job-dsl](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin) - JobDSL plugin. Required to generate jobs used for demo
  * [build-flow-plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin) - BuildFlow plugin. Required to orchestrate jobs execution
  * [buildgraph-view](https://wiki.jenkins-ci.org/display/JENKINS/Build+Graph+View+Plugin) - Build Graph View Plugin. We don't really need it for the demo. Just in case if I will need to show how jobs connected as a graph. 
* quietperiod.groovy - set global Quiet Period to 0
* executors.groovy - set number of executors on master to 0
* createjob.groovy - create a JobDsl seed job. This one is a tricky one. Apparently it is not that simple to create job in Jenkins on startup. There is a possibility to just copy config.xml. But IMHO doing that in Groovy is a bit more elegant despite of some effort required to look up type for the JobDSL builder. Also this way allows to avoid mess with file permission after copy of xml file. Main idea of this file is to create a seed job with JobDSL inside so we can generate all jobs during demo without wasting time on configuration.

## Setup on Mac OS and Windows

* Create new virtual machine using [docker-machine](https://docs.docker.com/installation/mac/)

```shell
  docker-machine create --driver virtualbox jenkins-checkpoints-demo
```

* Before you start you need to setup port forwarding from guest machine to your host so you can use your browser to see web pages from docker container. To do so open VirtualBox -> right click on jenkins-checkpoints-demo virtual machine -> Settings -> Network -> Port Forwarding -> Add one more rule that looks like ssh one but use 8080 as port number in both cases
* Jump into virtual machine by running

```shell
  docker-machine ssh jenkins-checkpoints-demo
```

* Clone this repo or transfer existing one into virtual machine using docker-machine scp

```shell
  git clone https://github.com/Andrey9kin/jenkins-checkpoints-demo.git
```

* Build docker image

```shell
  cd jenkins-checkpoints-demo
  docker build -t checkpoints-demo .
```

* Kick off new container
 
```shell
  cd jenkins-checkpoints-demo
  docker run -p 8080:8080 checkpoints-demo
```

* After few seconds you should be able to see Jenkins page in your browser at localhost:8080! Time to do things!

## Demo

* Go to localhost:8080 and run seedjob. It will generate all necessary jobs. We have two types of jobs. trigger is a buildflow that orchestrate jobs execution and step-x jobs. Those are just dummy ones that will hang for a random number of seconds passed as parameter from the build flow.
* Open build flow configuration. You can see that we create a CheckPoint before starting step-5. That will allow us to sync jobs and ensure execution order. We also will set description for every run in order to provide better visibility to what flow this execution belongs to
* Kick off 10-20 trigger executions and watch order of step-4 executions. You will notice that some sessions will reach that step faster than other but still will wait for previous flows to finish before start step-5
* That is basically it. Now you only limited with you imagination :) Go hack it!

Parameterized Scheduler
=======================

A Jenkins Plugin to support setting parameters in the build schedule. Using multiple cron lines each ending with a % and some name=value pairs you can schedule your parameterized build to run with different parameters at different times. It may turn out that
[this improvement request JENKINS-16352](https://issues.jenkins-ci.org/browse/JENKINS-16352)
will be implemented in Jenkins core. Then this plugin will no longer be needed. My first thought is to try it out as a plugin.
This plugin now supports [declarative Jenkins pipelines](https://jenkins.io/doc/book/pipeline/syntax/#declarative-pipeline). See configuration example below.

## Installation ##
Clone or fork to build the .hpi yourself (_mvn package_) or download the .hpi from my release. Then follow the [Jenkins Plugin installation instructions](https://wiki.jenkins-ci.org/display/JENKINS/Plugins#Plugins-Howtoinstallplugins)

## Configuration ##

After you save your project with some parameters (yes, save, then go back into the config page) you will see
>Build periodically with parameters

in the *Build Triggers* section as shown here:

![Parameterized Schedular Config](https://raw.githubusercontent.com/jenkinsci/parameterized-scheduler-plugin/master/site/images/configurationexample.png)

## Configuration Example ##
The cron line before the _%_ symbol is processed the same as the jenkins core _Build periodically Schedule_. Leave a space. Put in a _%_. Then add the name=value pairs you need for your project build parameters.

The idea was born from the need to use a different environment. To use a different TestNG configuration xml. In this example the _env_ parameter will be set to int during the build triggered at 15 after each hour. Then _env_ will be qa when the build runs at half past.

```
  #lets run against the integration environment at 15 past the hour
  15 * * * * % env=int
  #run QA too
  30 * * * % env=qa
```


Yes, of course you can set multiple parameters. Lets say you have three parameters:
- furniture
- color
- name (with a default of fred)

Then you might want a schedule like the following:

```
  # leave spaces where you want them around the parameters. They'll be trimmed.
  # we let the build run with the default name
  5 * * * * % furniture=chair;color=black
  # now, let's override that default name and use Mr. Rubble.
  10 * * * * % furniture=desk; color=yellow; name=barney
```

## Declarative Pipeline Configuration Example ##

The parameterized cron trigger can be specified using the key `parameterizedSpecification` under the `parameterizedCron` under the [triggers directive](https://jenkins.io/doc/book/pipeline/syntax/#declarative-directives). The built in `cron` trigger is still available and is independent of `parameterizedCron`.

Example:

```
pipeline {
    agent any
    parameters {
      string(name: 'PLANET', defaultValue: 'Earth', description: 'Which planet are we on?')
      string(name: 'GREETING', defaultValue: 'Hello', description: 'How shall we greet?')
    }
    triggers {
        cron('* * * * *')
        parameterizedCron {
          parameterizedSpecification('''
# leave spaces where you want them around the parameters. They'll be trimmed.
# we let the build run with the default name
*/2 * * * * %GREETING=Hola;PLANET=Pluto
*/3 * * * * %PLANET=Mars
        ''')
        }
    }
    stages {
        stage('Example') {
            steps {
                echo "${GREETING} ${PLANET}"
                script { currentBuild.description = "${GREETING} ${PLANET}" }
            }
        }
    }
}
```

Example Output in Jenkins
=========================
![Example Output in Jenkins](https://raw.githubusercontent.com/jenkinsci/parameterized-scheduler-plugin/master/site/images/scheduledBuilds.PNG)


## Pipeline as code example

```
properties(
  parameters([
    string(name: 'PLANET', defaultValue: 'Earth', description: 'Which planet are we on?'),
    string(name: 'GREETING', defaultValue: 'Hello', description: 'How shall we greet?')
  ]),
  pipelineTriggers([
    parameterizedCron('*/2 * * * * %GREETING=Hola;PLANET=Pluto'),
    parameterizedCron('*/3 * * * * %PLANET=Mars')
  ])
)
```

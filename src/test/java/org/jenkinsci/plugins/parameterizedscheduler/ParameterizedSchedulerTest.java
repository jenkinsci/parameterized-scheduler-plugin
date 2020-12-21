package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.triggers.Trigger;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ParameterizedSchedulerTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void freestyle() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("foo", "lol")));
        assertThat(p.getLastCompletedBuild(), is(nullValue()));
        Trigger<Job> t = new ParameterizedTimerTrigger("* * * * *%foo=bar");
        t.start(p, true);
        p.addTrigger(t);
        new Cron().doRun();
        assertThat(p.isInQueue(), is(true));
        r.waitUntilNoActivity();
        assertThat(p.getLastCompletedBuild(), is(notNullValue()));
        assertThat((String) p.getLastCompletedBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), is("bar"));
    }

    @Test
    public void pipeline() throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("", true));
        WorkflowRun wfr = p.scheduleBuild2(0).get();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("foo", "lol")));
        Trigger<Job> t = new ParameterizedTimerTrigger("* * * * *%foo=bar");
        t.start(p, true);
        p.addTrigger(t);
        new Cron().doRun();
        r.waitUntilNoActivity();
        assertThat(p.getLastCompletedBuild(), is(not(wfr)));
        assertThat((String) p.getLastCompletedBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), is("bar"));
    }

    @Test
    public void scripted() throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("properties([\n" +
                "  parameters([\n" +
                "    string(name: 'foo', defaultValue: 'lol')\n" +
                "  ]),\n" +
                "  pipelineTriggers([\n" +
                "    parameterizedCron('* * * * *%foo=bar')\n" +
                "  ])\n" +
                "])", true));
        WorkflowRun wfr = r.buildAndAssertSuccess(p);
        new Cron().doRun();
        r.waitUntilNoActivity();
        assertThat(p.getLastCompletedBuild(), is(not(wfr)));
        assertThat((String) p.getLastCompletedBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), is("bar"));
    }

    @Test
    public void declarative() throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("pipeline {\n" +
                "    agent any\n" +
                "    parameters {\n" +
                "      string(name: 'foo', defaultValue: 'lol')\n" +
                "    }\n" +
                "    triggers {\n" +
                "        parameterizedCron('* * * * *%foo=bar')\n" +
                "    }\n" +
                "    stages {\n" +
                "        stage('Test') {\n" +
                "            steps {\n" +
                "                echo 'test'\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}", true));
        WorkflowRun wfr = r.buildAndAssertSuccess(p);
        new Cron().doRun();
        r.waitUntilNoActivity();
        assertThat(p.getLastCompletedBuild(), is(not(wfr)));
        assertThat((String) p.getLastCompletedBuild().getAction(ParametersAction.class).getParameter("foo").getValue(), is("bar"));
    }
}

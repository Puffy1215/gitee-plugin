package com.gitee.jenkins.publisher;

import static com.gitee.jenkins.publisher.TestUtility.BUILD_NUMBER;
import static com.gitee.jenkins.publisher.TestUtility.BUILD_URL;
import static com.gitee.jenkins.publisher.TestUtility.GITEE_CONNECTION_V5;
import static com.gitee.jenkins.publisher.TestUtility.OWNER_PATH;
import static com.gitee.jenkins.publisher.TestUtility.PROJECT_ID;
import static com.gitee.jenkins.publisher.TestUtility.PULL_REQUEST_IID;
import static com.gitee.jenkins.publisher.TestUtility.REPO_PATH;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static com.gitee.jenkins.publisher.TestUtility.preparePublisher;
import static com.gitee.jenkins.publisher.TestUtility.setupGiteeConnections;
import static com.gitee.jenkins.publisher.TestUtility.verifyMatrixAggregatable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.mockito.stubbing.Answer;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;

import com.gitee.jenkins.connection.GiteeConnectionProperty;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.StreamBuildListener;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildData;
import jakarta.ws.rs.core.MediaType;

@WithJenkins
@ExtendWith(MockServerExtension.class)
public class GiteeReleasePublisherTest {

    private static JenkinsRule jenkins;

    private static MockServerClient mockServerClient;
    private BuildListener listener;

    @BeforeAll
    static void setUp(JenkinsRule rule, MockServerClient client) throws Exception {
        jenkins = rule;
        mockServerClient = client;
        setupGiteeConnections(jenkins, client);
    }

    @BeforeEach
    void setUp() {
        listener = new StreamBuildListener(jenkins.createTaskListener().getLogger(), Charset.defaultCharset());
    }

    @AfterEach
    void tearDown() {
        mockServerClient.reset();
    }

    @Test
    void createReleaseSuccessTest() throws IOException {
        AbstractBuild build = mockBuild(GITEE_CONNECTION_V5, Result.SUCCESS);
        HttpRequest request = createRelease("v5");
        mockServerClient.when(request).respond(response().withStatusCode(200).withContentType(org.mockserver.model.MediaType.APPLICATION_JSON).withBody("{\"test\": \"test\"}"));
        performAndVerify(build, false, false);
        mockServerClient.verify(request);
    }

    private void performAndVerify(AbstractBuild build, boolean increment, boolean attachFiles) {
        GiteeReleasePublisher publisher = new GiteeReleasePublisher();
        publisher.setOwner(OWNER_PATH);
        publisher.setRepo(REPO_PATH);
        publisher.setTagName("1.0.0");
        publisher.setName("test");
        publisher.setIncrement(false);
        publisher.setArtifacts(false);

        publisher.perform(build, null, listener);

    }

    @SuppressWarnings("rawtypes")
    private AbstractBuild mockBuild(String giteeConnection, Result result, String... remoteUrls) throws IOException {
        AbstractBuild build = mock(AbstractBuild.class);
        BuildData buildData = mock(BuildData.class);
        ObjectId objectId = new ObjectId(0, 0, 0, 0, 0);
        when(buildData.getRemoteUrls()).thenReturn(new HashSet<>(Arrays.asList(remoteUrls)));
        when(build.getAction(BuildData.class)).thenReturn(buildData);
        when(build.getResult()).thenReturn(result);
        when(build.getUrl()).thenReturn(BUILD_URL);
        when(build.getResult()).thenReturn(result);
        when(build.getNumber()).thenReturn(BUILD_NUMBER);
        when(buildData.getLastBuiltRevision()).thenReturn(new Revision(objectId));

        AbstractProject<?, ?> project = mock(AbstractProject.class);
        GitSCM scm = mock(GitSCM.class);
        when(scm.getBuildData(build)).thenReturn(buildData);
        when(project.getScm()).thenReturn(scm);

        when(project.getProperty(GiteeConnectionProperty.class))
                .thenReturn(new GiteeConnectionProperty(giteeConnection));
        doReturn(project).when(build).getParent();
        doReturn(project).when(build).getProject();
        EnvVars environment = mock(EnvVars.class);
        when(environment.expand(anyString()))
                .thenAnswer((Answer<String>) invocation -> (String) invocation.getArguments()[0]);
        try {
            when(build.getEnvironment(any(TaskListener.class))).thenReturn(environment);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return build;
    }

    private HttpRequest createRelease(String apiLevel) {
        // JsonBody json = new JsonBody("tag_name=1.0.0&name=test&prerelease=false&target_commitish=0000000000000000000000000000000000000000");

        return request()
                .withPath(String.format("/gitee/api/%s/repos/%s/%s/releases", apiLevel, OWNER_PATH, REPO_PATH))
                .withMethod("POST")
                .withHeader("PRIVATE-TOKEN", "secret")
                .withBody("tag_name=1.0.0&name=test&prerelease=false&target_commitish=0000000000000000000000000000000000000000");
                
    }
}

/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org Greg Messner and other contributors.         *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.gitlab;

import org.aoju.bus.gitlab.GitLabApi.ApiVersion;
import org.aoju.bus.gitlab.models.SystemHook;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class implements the client side API for the GitLab System Hooks Keys API calls.
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
public class SystemHooksApi extends AbstractApi {

    public SystemHooksApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of all system hooks. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks</code></pre>
     *
     * @return a list of SystemHookEvent
     * @throws GitLabApiException if any exception occurs
     */
    public List<SystemHook> getSystemHooks() throws GitLabApiException {
        return (getSystemHooks(getDefaultPerPage()).all());
    }

    /**
     * Get a list of all system hooks using the specified page and per page settings.
     * This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks</code></pre>
     *
     * @param page    the page to get
     * @param perPage the number of deploy keys per page
     * @return the list of SystemHookEvent in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<SystemHook> getSystemHooks(int page, int perPage) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(page, perPage), "hooks");
        return (response.readEntity(new GenericType<List<SystemHook>>() {
        }));
    }

    /**
     * Get a Pager of all system hooks. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks</code></pre>
     *
     * @param itemsPerPage the number of SystemHookEvent instances that will be fetched per page
     * @return a Pager of SystemHookEvent
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<SystemHook> getSystemHooks(int itemsPerPage) throws GitLabApiException {
        return (new Pager<SystemHook>(this, SystemHook.class, itemsPerPage, null, "hooks"));
    }

    /**
     * Get a Stream of all system hooks. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks</code></pre>
     *
     * @return a Stream of SystemHookEvent
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<SystemHook> getSystemHookStream() throws GitLabApiException {
        return (getSystemHooks(getDefaultPerPage()).stream());
    }

    /**
     * Add a new system hook. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: POST /hooks</code></pre>
     *
     * @param url                   the hook URL, required
     * @param token                 secret token to validate received payloads, optional
     * @param pushEvents            when true, the hook will fire on push events, optional
     * @param tagPushEvents         when true, the hook will fire on new tags being pushed, optional
     * @param enablSsslVerification do SSL verification when triggering the hook, optional
     * @return an SystemHookEvent instance with info on the added system hook
     * @throws GitLabApiException if any exception occurs
     */
    public SystemHook addSystemHook(String url, String token, Boolean pushEvents,
                                    Boolean tagPushEvents, Boolean enablSsslVerification) throws GitLabApiException {

        if (url == null) {
            throw new RuntimeException("url cannot be null");
        }

        GitLabApiForm formData = new GitLabApiForm()
                .withParam("url", url, true)
                .withParam("token", token)
                .withParam("push_events", pushEvents)
                .withParam("tag_push_events", tagPushEvents)
                .withParam("enable_ssl_verification", enablSsslVerification);
        Response response = post(Response.Status.CREATED, formData, "hooks");
        return (response.readEntity(SystemHook.class));
    }

    /**
     * Deletes a system hook. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: DELETE /hooks/:hook_id</code></pre>
     *
     * @param hook the SystemHook instance to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteSystemHook(SystemHook hook) throws GitLabApiException {

        if (hook == null) {
            throw new RuntimeException("hook cannot be null");
        }

        deleteSystemHook(hook.getId());
    }

    /**
     * Deletes a system hook. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: DELETE /hooks/:hook_id</code></pre>
     *
     * @param hookId the ID of the system hook to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteSystemHook(Integer hookId) throws GitLabApiException {

        if (hookId == null) {
            throw new RuntimeException("hookId cannot be null");
        }

        Response.Status expectedStatus = (isApiVersion(ApiVersion.V3) ? Response.Status.OK : Response.Status.NO_CONTENT);
        delete(expectedStatus, null, "hooks", hookId);
    }

    /**
     * Test a system hook. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks/:hook_id</code></pre>
     *
     * @param hook the SystemHookEvent instance to test
     * @throws GitLabApiException if any exception occurs
     */
    public void testSystemHook(SystemHook hook) throws GitLabApiException {

        if (hook == null) {
            throw new RuntimeException("hook cannot be null");
        }

        testSystemHook(hook.getId());
    }

    /**
     * Test a system hook. This method requires admin access.
     *
     * <pre><code>GitLab Endpoint: GET /hooks/:hook_id</code></pre>
     *
     * @param hookId the ID of the system hook to test
     * @throws GitLabApiException if any exception occurs
     */
    public void testSystemHook(Integer hookId) throws GitLabApiException {

        if (hookId == null) {
            throw new RuntimeException("hookId cannot be null");
        }

        get(Response.Status.OK, null, "hooks", hookId);
    }

}

/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.favorite.web;

import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteListenerService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.modules.favorite.service.validator.FavoriteValidationManagementService;
import fr.paris.lutece.plugins.extend.modules.favorite.util.constants.FavoriteConstants;
import fr.paris.lutece.plugins.extend.service.ExtendPlugin;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Front office application adding a resource to the favorites of the user, or removing it.
 */
@RequestScoped
@Named( FavoriteApp.BEAN_NAME )
@Controller( xpageName = FavoriteApp.XPAGE_NAME, securityTokenEnabled = true )
public class FavoriteApp extends MVCApplication
{
    public static final String XPAGE_NAME = "extendfavorite";
    public static final String BEAN_NAME = "extendfavorite.xpage." + XPAGE_NAME;

    private static final long serialVersionUID = 1L;

    private static final String VIEW_HOME = "home";
    private static final String CONSTANT_HTTP = "http";

    @Inject
    private transient IResourceExtenderHistoryService _resourceExtenderHistoryService;

    @Inject
    @Named( FavoriteService.BEAN_SERVICE )
    private transient IFavoriteService _favoriteService;

    /**
     * Redirects to the portal home: this application has no page of its own.
     *
     * @param request
     *            the HTTP request
     * @return the redirection
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage getHome( HttpServletRequest request )
    {
        return redirect( request, getPortalHomeUrl( request ) );
    }

    /**
     * Adds the resource to the favorites of the signed-in user, then redirects to the page of the resource.
     *
     * @param request
     *            the HTTP request
     * @return the redirection
     * @throws SiteMessageException
     *             if the request is invalid or the favorites are closed
     * @throws UserNotSignedException
     *             if no user is signed in
     */
    @Action( FavoriteConstants.ACTION_DO_FAVORITE )
    public XPage doFavorite( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String strIdExtendableResource = request.getParameter( FavoriteConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FavoriteConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strFavoriteValue = request.getParameter( FavoriteConstants.PARAMETER_FAVORITE_VALUE );

        int nFavoriteValue = NumberUtils.toInt( strFavoriteValue, Integer.MIN_VALUE );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType ) || nFavoriteValue == Integer.MIN_VALUE )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        String strSessionKeyNextUrl = getSessionKeyUrlRedirect( strIdExtendableResource, strExtendableResourceType );
        String strNextUrl = (String) request.getSession( ).getAttribute( strSessionKeyNextUrl );

        if ( StringUtils.isEmpty( strNextUrl ) )
        {
            strNextUrl = getRefererUrl( request );
        }
        else
        {
            request.getSession( ).removeAttribute( strSessionKeyNextUrl );
        }

        LuteceUser user = getSignedUser( request );

        String strErrorUrl = FavoriteValidationManagementService.validateFavorite( request, user, strIdExtendableResource, strExtendableResourceType,
                nFavoriteValue );

        if ( StringUtils.isNotEmpty( strErrorUrl ) )
        {
            if ( !strErrorUrl.startsWith( CONSTANT_HTTP ) )
            {
                strErrorUrl = AppPathService.getBaseUrl( request ) + strErrorUrl;
            }

            request.getSession( ).setAttribute( strSessionKeyNextUrl, strNextUrl );

            return redirect( request, strErrorUrl );
        }

        if ( !isFavorite( user, strIdExtendableResource, strExtendableResourceType ) )
        {
            if ( !FavoriteListenerService.canFavorite( strExtendableResourceType, strIdExtendableResource, user ) )
            {
                SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
            }

            _favoriteService.doFavorite( strIdExtendableResource, strExtendableResourceType, nFavoriteValue, request );
        }

        return redirect( request, strNextUrl );
    }

    /**
     * Removes the resource from the favorites of the signed-in user, then redirects to the page of the resource.
     *
     * @param request
     *            the HTTP request
     * @return the redirection
     * @throws SiteMessageException
     *             if the request is invalid or the favorites are closed
     * @throws UserNotSignedException
     *             if no user is signed in
     */
    @Action( FavoriteConstants.ACTION_CANCEL_FAVORITE )
    public XPage doCancelFavorite( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String strIdExtendableResource = request.getParameter( FavoriteConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FavoriteConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType ) )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        LuteceUser user = getSignedUser( request );

        if ( !FavoriteListenerService.canFavorite( strExtendableResourceType, strIdExtendableResource, user ) )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
        }

        _favoriteService.doCancelFavorite( user, strIdExtendableResource, strExtendableResourceType, request );

        return redirect( request, getRefererUrl( request ) );
    }

    /**
     * Get the session key of the URL to redirect the user to after he has added the resource to his favorites.
     *
     * @param strIdResource
     *            The id of the resource
     * @param strResourceType
     *            The type of the resource
     * @return The session key
     */
    public static String getSessionKeyUrlRedirect( String strIdResource, String strResourceType )
    {
        return ExtendPlugin.PLUGIN_NAME + FavoriteConstants.PARAMETER_FROM_URL + strResourceType + strIdResource;
    }

    /**
     * Gives the signed-in user.
     *
     * @param request
     *            the HTTP request
     * @return the user
     * @throws UserNotSignedException
     *             if no user is signed in
     */
    private static LuteceUser getSignedUser( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( ).getRegisteredUser( request ) : null;

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        return user;
    }

    /**
     * Tells whether the user already has the resource in his favorites.
     *
     * @param user
     *            the user
     * @param strIdExtendableResource
     *            the resource id
     * @param strExtendableResourceType
     *            the resource type
     * @return true when a favorite of the user exists
     */
    private boolean isFavorite( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType )
    {
        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter( );
        filter.setExtenderType( FavoriteResourceExtender.RESOURCE_EXTENDER );
        filter.setUserGuid( user.getName( ) );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );

        return !_resourceExtenderHistoryService.findByFilter( filter ).isEmpty( );
    }

    /**
     * Gives the page the request comes from, carrying the origin URL kept in session, or the portal home.
     *
     * @param request
     *            the HTTP request
     * @return the URL to go back to
     */
    private static String getRefererUrl( HttpServletRequest request )
    {
        String strReferer = request.getHeader( FavoriteConstants.PARAMETER_HTTP_REFERER );

        if ( strReferer == null )
        {
            return getPortalHomeUrl( request );
        }

        UrlItem url = new UrlItem( strReferer );
        String strFromUrl = (String) request.getSession( ).getAttribute( ExtendPlugin.PLUGIN_NAME + FavoriteConstants.PARAMETER_FROM_URL );

        if ( StringUtils.isNotEmpty( strFromUrl ) && !url.getUrl( ).contains( FavoriteConstants.PARAMETER_FROM_URL ) )
        {
            url.addParameter( FavoriteConstants.PARAMETER_FROM_URL, strFromUrl.replace( "%", "%25" ) );
        }

        return url.getUrl( );
    }

    /**
     * Gives the absolute URL of the portal home.
     *
     * @param request
     *            the HTTP request
     * @return the URL
     */
    private static String getPortalHomeUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( );
    }
}

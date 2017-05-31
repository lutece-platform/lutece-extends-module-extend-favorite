/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the favoriteing conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the favoriteing disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the favoriteing disclaimer in the documentation and/or other materials
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

import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteListenerService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.modules.favorite.service.validator.FavoriteValidationManagementService;
import fr.paris.lutece.plugins.extend.modules.favorite.util.constants.FavoriteConstants;
import fr.paris.lutece.plugins.extend.service.ExtendPlugin;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.service.extender.history.ResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FavoriteJspBean
 */
public class FavoriteJspBean
{
    public static final String URL_JSP_DO_FAVORITE = "jsp/site/plugins/extend/modules/favorite/DoFavorite.jsp";

    // TEMPLATES
    private static final String CONSTANT_HTTP = "http";

    // SERVICES
    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService = SpringContextService.getBean( ResourceExtenderHistoryService.BEAN_SERVICE );;
    private IFavoriteService _favoriteService = SpringContextService.getBean( FavoriteService.BEAN_SERVICE );

    /**
     * Update the favorite value an count. This method is called in FO by the favoriteing JSP :
     * <strong>jsp/site/plugins/extend/modules/favorite/DoFavorite.Jsp</strong>
     *
     * @param request
     *            The HTTP request
     * @param response
     *            The HTTP response
     * @throws IOException
     *             the io exception
     * @throws SiteMessageException
     *             the site message exception
     * @throws UserNotSignedException
     *             If the user has not signed in
     */
    public void doFavorite( HttpServletRequest request, HttpServletResponse response ) throws IOException, SiteMessageException, UserNotSignedException
    {
        String strIdExtendableResource = request.getParameter( FavoriteConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FavoriteConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strFavoriteValue = request.getParameter( FavoriteConstants.PARAMETER_FAVORITE_VALUE );
        String strFromUrl = (String) request.getSession( ).getAttribute( ExtendPlugin.PLUGIN_NAME + FavoriteConstants.PARAMETER_FROM_URL );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType ) || StringUtils.isBlank( strFavoriteValue ) )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        String strSessionKeyNextUrl = getSessionKeyUrlRedirect( strIdExtendableResource, strExtendableResourceType );

        String strNextUrl = (String) request.getSession( ).getAttribute( strSessionKeyNextUrl );

        if ( StringUtils.isEmpty( strNextUrl ) )
        {
            strNextUrl = request.getHeader( FavoriteConstants.PARAMETER_HTTP_REFERER );

            if ( strNextUrl != null )
            {
                UrlItem url = new UrlItem( strNextUrl );

                if ( StringUtils.isNotEmpty( strFromUrl ) )
                {
                    strFromUrl = strFromUrl.replaceAll( "%", "%25" );
                    if ( !url.getUrl( ).contains( FavoriteConstants.PARAMETER_FROM_URL ) )
                    {
                        url.addParameter( FavoriteConstants.PARAMETER_FROM_URL, strFromUrl );
                    }
                }

                strNextUrl = url.getUrl( );
            }
            else
            {
                strNextUrl = AppPathService.getPortalUrl( );
            }
        }
        else
        {
            request.getSession( ).removeAttribute( strSessionKeyNextUrl );
        }

        int nFavoriteValue = 0;

        try
        {
            nFavoriteValue = Integer.parseInt( strFavoriteValue );
        }
        catch( NumberFormatException e )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        String strErrorUrl = FavoriteValidationManagementService.validateFavorite( request, SecurityService.getInstance( ).getRemoteUser( request ),
                strIdExtendableResource, strExtendableResourceType, nFavoriteValue );

        if ( StringUtils.isNotEmpty( strErrorUrl ) )
        {
            if ( !strErrorUrl.startsWith( CONSTANT_HTTP ) )
            {
                strErrorUrl = AppPathService.getBaseUrl( request ) + strErrorUrl;
            }

            request.getSession( ).setAttribute( strSessionKeyNextUrl, strNextUrl );

            response.sendRedirect( strErrorUrl );

            return;
        }

        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter( );

        filter.setExtenderType( FavoriteResourceExtender.RESOURCE_EXTENDER );
        filter.setUserGuid( SecurityService.getInstance( ).getRemoteUser( request ).getName( ) );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );

        List<ResourceExtenderHistory> listHistories = _resourceExtenderHistoryService.findByFilter( filter );

        if ( listHistories.isEmpty( ) )
        {
            if ( FavoriteListenerService.canFavorite( strExtendableResourceType, strIdExtendableResource, SecurityService.getInstance( )
                    .getRemoteUser( request ) ) )
            {
                _favoriteService.doFavorite( strIdExtendableResource, strExtendableResourceType, nFavoriteValue, request );
            }
            else
            {
                SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
            }
        }

        response.sendRedirect( strNextUrl );
    }

    /**
     * Cancel the favorite value This method is called in FO by the favoriteing JSP :
     * <strong>jsp/site/plugins/extend/modules/favorite/DoCancelFavorite.Jsp</strong>
     * 
     * @param request
     *            The HTTP request
     * @param response
     *            The HTTP response
     * @throws IOException
     *             the io exception
     * @throws SiteMessageException
     *             the site message exception
     */
    public void doCancelFavorite( HttpServletRequest request, HttpServletResponse response ) throws IOException, SiteMessageException
    {
        String strIdExtendableResource = request.getParameter( FavoriteConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FavoriteConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strFromUrl = (String) request.getSession( ).getAttribute( ExtendPlugin.PLUGIN_NAME + FavoriteConstants.PARAMETER_FROM_URL );
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType ) )
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }
        if ( FavoriteListenerService.canFavorite( strExtendableResourceType, strIdExtendableResource, user ) )
        {
            _favoriteService.doCancelFavorite( user, strIdExtendableResource, strExtendableResourceType, request );
        }
        else
        {
            SiteMessageService.setMessage( request, FavoriteConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
        }
        String strReferer = request.getHeader( FavoriteConstants.PARAMETER_HTTP_REFERER );

        if ( strReferer != null )
        {
            UrlItem url = new UrlItem( strReferer );

            if ( StringUtils.isNotEmpty( strFromUrl ) )
            {
                strFromUrl = strFromUrl.replaceAll( "%", "%25" );
                if ( !url.getUrl( ).contains( FavoriteConstants.PARAMETER_FROM_URL ) )
                {
                    url.addParameter( FavoriteConstants.PARAMETER_FROM_URL, strFromUrl );
                }
            }

            response.sendRedirect( url.getUrl( ) );
        }
        else
        {
            response.sendRedirect( AppPathService.getPortalUrl( ) );
        }
    }

    /**
     * Get the session key of the URL to redirect the user to after he has favorited for the resource
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
}

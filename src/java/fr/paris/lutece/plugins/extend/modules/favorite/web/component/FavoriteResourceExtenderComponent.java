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
package fr.paris.lutece.plugins.extend.modules.favorite.web.component;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfig;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.modules.favorite.util.constants.FavoriteConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.util.ExtendErrorException;
import fr.paris.lutece.plugins.extend.util.JSONUtils;
import fr.paris.lutece.plugins.extend.web.component.AbstractResourceExtenderComponent;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Renders the favorite add-on of a resource in the front office and its summary in the back office.
 */
@ApplicationScoped
public class FavoriteResourceExtenderComponent extends AbstractResourceExtenderComponent
{
    private static final String TEMPLATE_FAVORITE = "skin/plugins/extend/modules/favorite/favorite.html";
    private static final String TEMPLATE_FAVORITE_INFO = "admin/plugins/extend/modules/favorite/favorite_info.html";

    @Inject
    @Named( FavoriteService.BEAN_SERVICE )
    private IFavoriteService _favoriteService;

    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;

    @Inject
    @Named( FavoriteResourceExtender.BEAN_NAME )
    private IResourceExtender _resourceExtender;

    @Inject
    private ISecurityTokenService _securityTokenService;

    /**
     * {@inheritDoc}
     */
    @Override
    public IResourceExtender getResourceExtender( )
    {
        return _resourceExtender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildXmlAddOn( String strIdExtendableResource, String strExtendableResourceType, String strParameters, StringBuffer strXml )
    {
        // Nothing yet
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPageAddOn( String strIdExtendableResource, String strExtendableResourceType, String strParameters, HttpServletRequest request )
    {
        LuteceUser user = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( ).getRegisteredUser( request ) : null;
        String strTemplateContent = DatabaseTemplateService.getTemplateFromKey( FavoriteConstants.MARK_EXTEND_FAVORITE );

        Map<String, Object> model = new HashMap<>( );
        model.put( FavoriteConstants.MARK_FAVORITE, _favoriteService.findByResource( strIdExtendableResource, strExtendableResourceType ) );
        model.put( FavoriteConstants.MARK_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        model.put( FavoriteConstants.MARK_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );
        model.put( FavoriteConstants.MARK_SHOW, fetchShowParameter( strParameters ) );
        model.put( FavoriteConstants.MARK_CAN_FAVORITE, user != null );
        model.put( FavoriteConstants.MARK_FAVORITE_CLOSED, user == null );
        boolean bCanDelete = user != null && isFavoriteer( user, strIdExtendableResource, strExtendableResourceType );
        model.put( FavoriteConstants.MARK_CAN_DELETE_FAVORITE, bCanDelete );

        if ( bCanDelete )
        {
            model.put( FavoriteConstants.MARK_CANCEL_FAVORITE_TOKEN, _securityTokenService.getToken( request, FavoriteConstants.ACTION_CANCEL_FAVORITE ) );
        }
        else if ( user != null )
        {
            model.put( FavoriteConstants.MARK_FAVORITE_TOKEN, _securityTokenService.getToken( request, FavoriteConstants.ACTION_DO_FAVORITE ) );
        }

        if ( strTemplateContent != null )
        {
            model.put( FavoriteConstants.MARK_FAVORITE_HTML_CONTENT,
                    AppTemplateService.getTemplateFromStringFtl( strTemplateContent, request.getLocale( ), model ).getHtml( ) );
        }

        return AppTemplateService.getTemplate( TEMPLATE_FAVORITE, request.getLocale( ), model ).getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfoHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        if ( resourceExtender == null )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( FavoriteConstants.MARK_FAVORITE,
                _favoriteService.findByResource( resourceExtender.getIdExtendableResource( ), resourceExtender.getExtendableResourceType( ) ) );

        return AppTemplateService.getTemplate( TEMPLATE_FAVORITE_INFO, locale, model ).getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtenderConfig getConfig( int nIdExtender )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveConfig( HttpServletRequest request, IExtenderConfig config ) throws ExtendErrorException
    {
        // No configuration
    }

    /**
     * Reads the show parameter of the extender parameters.
     *
     * @param strParameters
     *            the extender parameters, in JSON
     * @return the show parameter, or an empty string
     */
    private static String fetchShowParameter( String strParameters )
    {
        String strShowParameter = StringUtils.EMPTY;
        ObjectNode jsonParameters = JSONUtils.parseParameters( strParameters );

        if ( jsonParameters != null )
        {
            if ( jsonParameters.has( FavoriteConstants.JSON_KEY_SHOW ) )
            {
                strShowParameter = jsonParameters.get( FavoriteConstants.JSON_KEY_SHOW ).asText( );
            }
            else
            {
                AppLogService.debug( "No {} found in {}", FavoriteConstants.JSON_KEY_SHOW, jsonParameters );
            }
        }

        return strShowParameter;
    }

    /**
     * Tells whether the user has the resource in his favorites.
     *
     * @param user
     *            the user
     * @param strIdExtendableResource
     *            the resource id
     * @param strExtendableResourceType
     *            the resource type
     * @return true when a favorite of the user exists
     */
    private boolean isFavoriteer( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType )
    {
        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter( );
        filter.setExtenderType( FavoriteResourceExtender.RESOURCE_EXTENDER );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );
        filter.setUserGuid( user.getName( ) );

        return !_resourceExtenderHistoryService.findByFilter( filter ).isEmpty( );
    }
}

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
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.business.Favorite;
import fr.paris.lutece.plugins.extend.modules.favorite.business.FavoriteFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.modules.favorite.util.constants.FavoriteConstants;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.util.ExtendErrorException;
import fr.paris.lutece.plugins.extend.util.JSONUtils;
import fr.paris.lutece.plugins.extend.web.component.AbstractResourceExtenderComponent;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * FavoriteResourceExtenderComponent
 *
 */
public class FavoriteResourceExtenderComponent extends AbstractResourceExtenderComponent
{
    // TEMPLATES
    private static final String TEMPLATE_FAVORITE = "skin/plugins/extend/modules/favorite/favorite.html";
    private static final String TEMPLATE_FAVORITE_INFO = "admin/plugins/extend/modules/favorite/favorite_info.html";
    @Inject
    private IFavoriteService _favoriteService;
    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;

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
    @SuppressWarnings( "deprecation" )
    @Override
    public String getPageAddOn( String strIdExtendableResource, String strExtendableResourceType, String strParameters, HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        Favorite favorite = _favoriteService.findByResource( strIdExtendableResource, strExtendableResourceType );
        String strTemplateContent = DatabaseTemplateService.getTemplateFromKey( FavoriteConstants.MARK_EXTEND_FAVORITE );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FavoriteConstants.MARK_FAVORITE, favorite );
        model.put( FavoriteConstants.MARK_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        model.put( FavoriteConstants.MARK_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );
        model.put( FavoriteConstants.MARK_SHOW, fetchShowParameter( strParameters ) );

        if ( user != null )
        {
            model.put( FavoriteConstants.MARK_CAN_FAVORITE, true );
            model.put( FavoriteConstants.MARK_FAVORITE_CLOSED, false );
            model.put( FavoriteConstants.MARK_CAN_DELETE_FAVORITE, isFavoriteer( user, strIdExtendableResource, strExtendableResourceType ) );
        }
        else
        {
            model.put( FavoriteConstants.MARK_CAN_FAVORITE, false );
            model.put( FavoriteConstants.MARK_FAVORITE_CLOSED, true );
        }

        model.put( FavoriteConstants.MARK_FAVORITE_HTML_CONTENT,
                AppTemplateService.getTemplateFromStringFtl( strTemplateContent, request.getLocale( ), model ).getHtml( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FAVORITE, request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfoHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        if ( resourceExtender != null )
        {
            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( FavoriteConstants.MARK_FAVORITE,
                    _favoriteService.findByResource( resourceExtender.getIdExtendableResource( ), resourceExtender.getExtendableResourceType( ) ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FAVORITE_INFO, request.getLocale( ), model );

            return template.getHtml( );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Fetch show parameter.
     *
     * @param strParameters
     *            the str parameters
     * @return the string
     */
    private String fetchShowParameter( String strParameters )
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
                AppLogService.debug( "No " + FavoriteConstants.JSON_KEY_SHOW + " found in " + jsonParameters );
            }
        }

        return strShowParameter;
    }

    /**
     *
     * @param user
     *            the User
     * @param strIdExtendableResource
     *            the IdExtendableResource
     * @param strExtendableResourceType
     *            the ExtendableResourceType
     * @return if the user is a favoriteer
     */
    private boolean isFavoriteer( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType )
    {
        boolean res = false;
        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter( );

        filter.setExtenderType( FavoriteResourceExtender.RESOURCE_EXTENDER );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );
        filter.setUserGuid( user.getName( ) );
        filter.setAscSort( false );
        filter.setSortedAttributeName( FavoriteConstants.ORDER_BY_DATE_CREATION );

        List<ResourceExtenderHistory> listHistories = _resourceExtenderHistoryService.findByFilter( filter );

        res = CollectionUtils.isNotEmpty( listHistories );

        return res;
    }

    @Override
    public String getConfigHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        return null;
    }

    @Override
    public IExtenderConfig getConfig( int nIdExtender )
    {
        return null;
    }

    @Override
    public void doSaveConfig( HttpServletRequest request, IExtenderConfig config ) throws ExtendErrorException
    {
    }
}

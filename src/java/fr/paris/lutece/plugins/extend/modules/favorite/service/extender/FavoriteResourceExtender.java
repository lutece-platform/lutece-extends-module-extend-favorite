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
package fr.paris.lutece.plugins.extend.modules.favorite.service.extender;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.IFavoriteService;
import fr.paris.lutece.plugins.extend.service.extender.AbstractResourceExtender;

import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * FavoriteResourceExtender
 */
public class FavoriteResourceExtender extends AbstractResourceExtender
{
    /** The Constant RESOURCE_EXTENDER. */
    public static final String RESOURCE_EXTENDER = "favorite";
    @Inject
    @Named( FavoriteService.BEAN_SERVICE )
    private IFavoriteService _favoriteService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( String strExtenderType )
    {
        if ( StringUtils.isNotBlank( strExtenderType ) )
        {
            return getKey( ).equals( strExtenderType );
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContent( String strIdExtendableResource, String strExtendableResourceType, String strParameters, HttpServletRequest request )
    {
        return getResourceExtenderComponent( ).getPageAddOn( strIdExtendableResource, strExtendableResourceType, strParameters, request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doCreateResourceAddOn( ResourceExtenderDTO extender )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doDeleteResourceAddOn( ResourceExtenderDTO extender )
    {
    }
}

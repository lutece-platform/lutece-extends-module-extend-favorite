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
package fr.paris.lutece.plugins.extend.modules.favorite.service;

import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.business.Favorite;
import fr.paris.lutece.plugins.extend.modules.favorite.business.FavoriteFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.business.FavoriteHistory;
import fr.paris.lutece.plugins.extend.modules.favorite.business.IFavoriteDAO;
import fr.paris.lutece.plugins.extend.modules.favorite.service.FavoriteListenerService;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.security.LuteceUser;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * favoriteService
 *
 */
public class FavoriteService implements IFavoriteService
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "extendfavorite.favoriteService";
    @Inject
    private IFavoriteDAO _favoriteDAO;
    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;
    @Inject
    private IFavoriteHistoryService _favoriteHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public void create( Favorite favorite )
    {
        _favoriteDAO.insert( favorite, FavoritePlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public void update( Favorite favorite )
    {
        _favoriteDAO.store( favorite, FavoritePlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public synchronized void doFavorite( String strIdExtendableResource, String strExtendableResourceType, int nVoteValue, HttpServletRequest request )
    {
        Favorite favorite = findByResource( strIdExtendableResource, strExtendableResourceType );

        // Create the favorite if not exists
        if ( favorite == null )
        {
            favorite = new Favorite( );
            favorite.setIdExtendableResource( strIdExtendableResource );
            favorite.setExtendableResourceType( strExtendableResourceType );
            favorite.setFavoriteCount( 1 );
            create( favorite );
        }
        else
        {
            favorite.setFavoriteCount( favorite.getFavoriteCount( ) + 1 );
            update( favorite );
        }

        ResourceExtenderHistory history = _resourceExtenderHistoryService.create( FavoriteResourceExtender.RESOURCE_EXTENDER, strIdExtendableResource,
                strExtendableResourceType, request );

        FavoriteHistory favoriteHistory = new FavoriteHistory( );
        favoriteHistory.setIdExtenderHistory( history.getIdHistory( ) );
        favoriteHistory.setFavoriteValue( nVoteValue );
        _favoriteHistoryService.create( favoriteHistory );
        // Call Listener
        FavoriteListenerService.favorite( strExtendableResourceType, strIdExtendableResource, request );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public synchronized void doCancelFavorite( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType, HttpServletRequest request )
    {
        ResourceExtenderHistoryFilter resourceExtenderHistoryFilter = new ResourceExtenderHistoryFilter( );
        resourceExtenderHistoryFilter.setUserGuid( user.getName( ) );
        resourceExtenderHistoryFilter.setIdExtendableResource( strIdExtendableResource );
        resourceExtenderHistoryFilter.setExtendableResourceType( strExtendableResourceType );
        resourceExtenderHistoryFilter.setExtenderType( FavoriteResourceExtender.RESOURCE_EXTENDER );

        List<ResourceExtenderHistory> histories = _resourceExtenderHistoryService.findByFilter( resourceExtenderHistoryFilter );

        if ( CollectionUtils.isNotEmpty( histories ) )
        {
            for ( ResourceExtenderHistory history : histories )
            {
                FavoriteHistory favoriteHistory = _favoriteHistoryService.findByHistoryExtenderId( history.getIdHistory( ) );

                if ( favoriteHistory != null )
                {
                    _favoriteHistoryService.remove( favoriteHistory.getIdFavoriteHistory( ) );

                    Favorite favorite = findByResource( strIdExtendableResource, strExtendableResourceType );
                    favorite.setFavoriteCount( favorite.getFavoriteCount( ) - 1 );
                    update( favorite );
                    // Call Listener
                    FavoriteListenerService.cancelFavorite( strExtendableResourceType, strIdExtendableResource, request );

                }

                _resourceExtenderHistoryService.remove( Integer.valueOf( "" + history.getIdHistory( ) ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public void remove( int nIdFavorite )
    {
        _favoriteDAO.delete( nIdFavorite, FavoritePlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FavoritePlugin.TRANSACTION_MANAGER )
    public void removeByResource( String strIdExtendableResource, String strExtendableResourceType )
    {
        _favoriteDAO.deleteByResource( strIdExtendableResource, strExtendableResourceType, FavoritePlugin.getPlugin( ) );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Favorite findByPrimaryKey( int nIdFavorite )
    {
        return _favoriteDAO.load( nIdFavorite, FavoritePlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Favorite findByResource( String strIdExtendableResource, String strExtendableResourceType )
    {
        return _favoriteDAO.loadByResource( strIdExtendableResource, strExtendableResourceType, FavoritePlugin.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Favorite> findByFilter( FavoriteFilter filter )
    {
        return _favoriteDAO.loadByFilter( filter, FavoritePlugin.getPlugin( ) );
    }
}

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.extend.modules.favorite.business.Favorite;
import fr.paris.lutece.plugins.extend.modules.favorite.business.FavoriteFilter;
import fr.paris.lutece.plugins.extend.modules.favorite.business.FavoriteHistory;
import fr.paris.lutece.plugins.extend.modules.favorite.service.extender.FavoriteResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.AbstractResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtender;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Favorite service, history service, listeners and extender tests.
 */
public class FavoriteServiceTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "unit_test_favorite";
    private static final String RESOURCE_TYPE_REFUSED = "unit_test_favorite_refused";
    private static final long ID_EXTENDER_HISTORY = 987654L;

    @Inject
    @Named( FavoriteService.BEAN_SERVICE )
    private IFavoriteService _favoriteService;

    @Inject
    @Named( FavoriteHistoryService.BEAN_SERVICE )
    private IFavoriteHistoryService _favoriteHistoryService;

    @Inject
    @Named( FavoriteResourceExtender.BEAN_NAME )
    private IResourceExtender _resourceExtender;

    /**
     * Creates, updates, finds and removes the counters of two resources.
     */
    @Test
    public void testFavoriteLifecycle( )
    {
        Favorite first = newFavorite( "1" );
        Favorite second = newFavorite( "2" );
        _favoriteService.create( first );
        _favoriteService.create( second );

        assertTrue( first.getIdFavorite( ) > 0, "generated id" );
        assertTrue( second.getIdFavorite( ) > first.getIdFavorite( ), "second id" );

        Favorite stored = _favoriteService.findByResource( "1", RESOURCE_TYPE );
        assertNotNull( stored, "stored" );
        assertEquals( 1, stored.getFavoriteCount( ), "count" );

        stored.setFavoriteCount( 5 );
        _favoriteService.update( stored );
        assertEquals( 5, _favoriteService.findByPrimaryKey( first.getIdFavorite( ) ).getFavoriteCount( ), "updated count" );

        FavoriteFilter filter = new FavoriteFilter( );
        filter.setExtendableResourceType( RESOURCE_TYPE );
        assertEquals( 2, _favoriteService.findByFilter( filter ).size( ), "by filter" );

        _favoriteService.removeByResource( "1", RESOURCE_TYPE );
        assertNull( _favoriteService.findByResource( "1", RESOURCE_TYPE ), "removed by resource" );
        assertNotNull( _favoriteService.findByResource( "2", RESOURCE_TYPE ), "other resource kept" );

        _favoriteService.remove( second.getIdFavorite( ) );
        assertNull( _favoriteService.findByPrimaryKey( second.getIdFavorite( ) ), "removed" );
    }

    /**
     * Creates, finds and removes a favorite history.
     */
    @Test
    public void testFavoriteHistory( )
    {
        FavoriteHistory history = new FavoriteHistory( );
        history.setIdExtenderHistory( ID_EXTENDER_HISTORY );
        history.setFavoriteValue( 1 );
        _favoriteHistoryService.create( history );
        assertTrue( history.getIdFavoriteHistory( ) > 0, "generated id" );

        FavoriteHistory stored = _favoriteHistoryService.findByHistoryExtenderId( ID_EXTENDER_HISTORY );
        assertNotNull( stored, "stored" );
        assertEquals( 1, stored.getFavoriteValue( ), "value" );

        _favoriteHistoryService.remove( stored.getIdFavoriteHistory( ) );
        assertNull( _favoriteHistoryService.findByHistoryExtenderId( ID_EXTENDER_HISTORY ), "removed" );
    }

    /**
     * Allows a favorite without listener, refuses it when a listener of the resource type refuses.
     */
    @Test
    public void testCanFavorite( )
    {
        assertTrue( FavoriteListenerService.canFavorite( RESOURCE_TYPE, "1", null ), "no listener" );

        FavoriteListenerService.registerListener( RESOURCE_TYPE_REFUSED, new RefusingListener( ) );
        assertFalse( FavoriteListenerService.canFavorite( RESOURCE_TYPE_REFUSED, "1", null ), "refused" );
        assertTrue( FavoriteListenerService.canFavorite( RESOURCE_TYPE, "1", null ), "other type" );
    }

    /**
     * Checks the extender is discovered with its key and options.
     */
    @Test
    public void testExtender( )
    {
        assertEquals( FavoriteResourceExtender.RESOURCE_EXTENDER, _resourceExtender.getKey( ), "key" );
        assertTrue( _resourceExtender.isInvoked( FavoriteResourceExtender.RESOURCE_EXTENDER ), "invoked" );
        assertTrue( _resourceExtender.isHistoryEnable( ), "history" );
        assertFalse( _resourceExtender.isConfigRequired( ), "no config" );
        assertNotNull( ( (AbstractResourceExtender) _resourceExtender ).getResourceExtenderComponent( ), "component" );
    }

    /**
     * Builds a favorite of the test resource type.
     *
     * @param strIdResource
     *            the resource id
     * @return the favorite
     */
    private static Favorite newFavorite( String strIdResource )
    {
        Favorite favorite = new Favorite( );
        favorite.setIdExtendableResource( strIdResource );
        favorite.setExtendableResourceType( RESOURCE_TYPE );
        favorite.setFavoriteCount( 1 );

        return favorite;
    }

    /**
     * Listener refusing every favorite.
     */
    private static final class RefusingListener implements IFavoriteListener
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void favorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
        {
            // Nothing to record
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void cancelFavorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
        {
            // Nothing to record
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canFavorite( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user )
        {
            return false;
        }
    }
}

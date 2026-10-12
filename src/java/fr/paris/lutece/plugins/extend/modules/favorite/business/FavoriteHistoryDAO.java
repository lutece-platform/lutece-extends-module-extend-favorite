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
package fr.paris.lutece.plugins.extend.modules.favorite.business;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class provides Data Access methods for Favorite objects.
 */
@ApplicationScoped
public class FavoriteHistoryDAO implements IFavoriteHistoryDAO
{
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_favorite_history ( id_extender_history, favorite_value ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_FIND_BY_EXTENDER_HISTORY_ID = " SELECT id_favorite_history, id_extender_history, favorite_value FROM extend_favorite_history WHERE id_extender_history = ?";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = " DELETE FROM extend_favorite_history WHERE id_extender_history "
            + " IN ( SELECT id_history FROM extend_resource_extender_history WHERE extender_type = 'favorite' AND resource_type = ? ";
    private static final String SQL_QUERY_FILTER_ID_RESOURCE = " AND id_resource = ? ";
    private static final String SQL_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_favorite_history WHERE id_favorite_history = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        boolean bOneResource = !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource );
        String strSql = SQL_QUERY_DELETE_BY_RESOURCE + ( bOneResource ? SQL_QUERY_FILTER_ID_RESOURCE : "" ) + SQL_CLOSE_PARENTHESIS;

        try ( DAOUtil daoUtil = new DAOUtil( strSql, plugin ) )
        {
            daoUtil.setString( 1, strExtendableResourceType );

            if ( bOneResource )
            {
                daoUtil.setString( 2, strIdExtendableResource );
            }

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( FavoriteHistory favoriteHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setLong( 1, favoriteHistory.getIdExtenderHistory( ) );
            daoUtil.setInt( 2, favoriteHistory.getFavoriteValue( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                favoriteHistory.setIdFavoriteHistory( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FavoriteHistory findByHistoryExtenderId( long lIdHistoryExtenderId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_EXTENDER_HISTORY_ID, plugin ) )
        {
            daoUtil.setLong( 1, lIdHistoryExtenderId );
            daoUtil.executeQuery( );

            FavoriteHistory favoriteHistory = null;

            if ( daoUtil.next( ) )
            {
                favoriteHistory = new FavoriteHistory( );
                favoriteHistory.setIdFavoriteHistory( daoUtil.getInt( 1 ) );
                favoriteHistory.setIdExtenderHistory( daoUtil.getLong( 2 ) );
                favoriteHistory.setFavoriteValue( daoUtil.getInt( 3 ) );
            }

            return favoriteHistory;
        }
    }
}

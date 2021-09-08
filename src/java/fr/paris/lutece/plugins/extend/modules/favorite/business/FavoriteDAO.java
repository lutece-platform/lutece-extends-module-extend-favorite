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

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for favorite objects.
 */
public class FavoriteDAO implements IFavoriteDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_favorite ) FROM extend_favorite ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_favorite ( id_favorite, id_resource, resource_type, favorite_count ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_favorite, id_resource, resource_type, favorite_count FROM extend_favorite ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_favorite = ? ";
    private static final String SQL_QUERY_SELECT_BY_RESOURCE = SQL_QUERY_SELECT_ALL + " WHERE id_resource = ? AND resource_type = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_favorite WHERE id_favorite = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = " DELETE FROM extend_favorite WHERE resource_type = ? ";
    private static final String SQL_QUERY_FILTER_ID_RESOURCE = " AND id_resource = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE extend_favorite SET id_resource = ?, resource_type = ?, favorite_count = ? WHERE id_favorite = ?  ";

    /**
     * Generates a new primary key.
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Favorite favorite, Plugin plugin )
    {
        int nNewPrimaryKey = newPrimaryKey( plugin );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        favorite.setIdFavorite( nNewPrimaryKey );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, favorite.getIdFavorite( ) );
        daoUtil.setString( nIndex++, favorite.getIdExtendableResource( ) );
        daoUtil.setString( nIndex++, favorite.getExtendableResourceType( ) );
        daoUtil.setInt( nIndex, favorite.getFavoriteCount( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Favorite load( int nIdfavorite, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdfavorite );
        daoUtil.executeQuery( );

        Favorite favorite = null;

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            favorite = new Favorite( );
            favorite.setIdFavorite( daoUtil.getInt( nIndex++ ) );
            favorite.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            favorite.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            favorite.setFavoriteCount( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return favorite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdfavorite, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdfavorite );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        int nIndex = 1;
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_DELETE_BY_RESOURCE );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            sbSql.append( SQL_QUERY_FILTER_ID_RESOURCE );
        }

        DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin );
        daoUtil.setString( nIndex++, strExtendableResourceType );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            daoUtil.setString( nIndex, strIdExtendableResource );
        }

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Favorite favorite, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( nIndex++, favorite.getIdExtendableResource( ) );
        daoUtil.setString( nIndex++, favorite.getExtendableResourceType( ) );
        daoUtil.setInt( nIndex++, favorite.getFavoriteCount( ) );

        daoUtil.setInt( nIndex, favorite.getIdFavorite( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Favorite loadByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        Favorite favorite = null;

        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESOURCE, plugin );
        daoUtil.setString( nIndex++, strIdExtendableResource );
        daoUtil.setString( nIndex, strExtendableResourceType );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nIndex = 1;

            favorite = new Favorite( );
            favorite.setIdFavorite( daoUtil.getInt( nIndex++ ) );
            favorite.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            favorite.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            favorite.setFavoriteCount( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return favorite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Favorite> loadByFilter( FavoriteFilter filter, Plugin plugin )
    {
        List<Favorite> listFavorite = new ArrayList<Favorite>( );
        DAOUtil daoUtil = new DAOUtil( filter.buildSQLQuery( SQL_QUERY_SELECT_ALL ), plugin );
        filter.setFilterValues( daoUtil );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            Favorite favorite = new Favorite( );
            favorite.setIdFavorite( daoUtil.getInt( nIndex++ ) );
            favorite.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            favorite.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            favorite.setFavoriteCount( daoUtil.getInt( nIndex ) );

            listFavorite.add( favorite );
        }

        daoUtil.free( );

        return listFavorite;
    }
}

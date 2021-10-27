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

import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 *
 * Favoriteer filter
 *
 */
public class FavoriteFilter implements Serializable
{
    /** The Constant ALL. */
    public static final int ALL = -1;
    private static final long serialVersionUID = -3449479672496815070L;

    // SQL
    private static final String SQL_WHERE = " WHERE ";
    private static final String SQL_AND = " AND ";
    private static final String SQL_OR = " OR ";

    // FILTERS
    private static final String SQL_FILTER_ID_FAVORITE = " id_favorite = ? ";
    private static final String SQL_FILTER_ID_RESOURCE = " id_resource = ? ";
    private static final String SQL_FILTER_RESOURCE_TYPE = " resource_type = ? ";
    private int _nIdFavorite;
    private String _strIdExtendableResource;
    private String _strExtendableResourceType;
    private boolean _bIsWideSearch;

    /**
     * CONSTRUCTOR INIT
     */
    public FavoriteFilter( )
    {
        _nIdFavorite = ALL;
        _strIdExtendableResource = StringUtils.EMPTY;
        _strExtendableResourceType = StringUtils.EMPTY;
        _bIsWideSearch = false;
    }

    /**
     * Get Id ExtendableResource
     * 
     * @return Id ExtendableResource
     */
    public String getIdExtendableResource( )
    {
        return _strIdExtendableResource;
    }

    /**
     * Set Id ExtendableResource
     * 
     * @param strIdExtendableResource
     *            the Id ExtendableResource
     */
    public void setIdExtendableResource( String strIdExtendableResource )
    {
        this._strIdExtendableResource = strIdExtendableResource;
    }

    /**
     * GET ExtendableResourceType
     * 
     * @return Extendable Resource Type
     */
    public String getExtendableResourceType( )
    {
        return _strExtendableResourceType;
    }

    /**
     * SET the extendable resource type
     * 
     * @param strExtendableResourceType
     *            the resource type
     */
    public void setExtendableResourceType( String strExtendableResourceType )
    {
        this._strExtendableResourceType = strExtendableResourceType;
    }

    /**
     * Get Id favorite
     * 
     * @return Id favorite
     */
    public int getIdFavorite( )
    {
        return _nIdFavorite;
    }

    /**
     * Set the Id favorite
     * 
     * @param nIdFavorite
     *            Id favorite
     */
    public void setIdFavorite( int nIdFavorite )
    {
        this._nIdFavorite = nIdFavorite;
    }

    /**
     * Check if the filter is applied to a wide search or not. <br/>
     * In other words, the SQL query will use
     * <ul>
     * <li>SQL <b>OR</b> if it is applied to a wide search</li>
     * <li>SQL <b>AND</b> if it is not applied to a wide search</li>
     * </ul>
     * 
     * @return true if it is applied to a wide search
     */
    public boolean isWideSearch( )
    {
        return _bIsWideSearch;
    }

    /**
     * Set true if the filter is applied to a wide search. <br/>
     * In other words, the SQL query will use
     * <ul>
     * <li>SQL <b>OR</b> if it is applied to a wide search</li>
     * <li>SQL <b>AND</b> if it is not applied to a wide search</li>
     * </ul>
     * 
     * @param bIsWideSearch
     *            true if it a wide search, false otherwise
     */
    public void setIsWideSearch( boolean bIsWideSearch )
    {
        this._bIsWideSearch = bIsWideSearch;
    }

    /**
     * Contains IdFavorite.
     *
     * @return true, if successful
     */
    public boolean containsIdFavorite( )
    {
        return _nIdFavorite != ALL;
    }

    /**
     * Contains Id ExtendableResource.
     *
     * @return true, if successful
     */
    public boolean containsIdExtendableResource( )
    {
        return StringUtils.isNotBlank( _strIdExtendableResource );
    }

    /**
     * Contains Id ExtendableResource.
     *
     * @return true, if successful
     */
    public boolean containsExtendableResourceType( )
    {
        return StringUtils.isNotBlank( _strExtendableResourceType );
    }

    /**
     * Builds the filter.
     *
     * @param sbSQL
     *            the sb sql
     * @param bAddFilter
     *            the b add filter
     * @param strSQL
     *            the str sql
     * @param nIndex
     *            the n index
     * @return the int
     */
    private int buildFilter( StringBuilder sbSQL, boolean bAddFilter, String strSQL, int nIndex )
    {
        int nIndexTmp = nIndex;

        if ( bAddFilter )
        {
            nIndexTmp = addSQLWhereOr( isWideSearch( ), sbSQL, nIndex );
            sbSQL.append( strSQL );
        }

        return nIndexTmp;
    }

    /**
     * Add a <b>WHERE</b> or a <b>OR</b> depending of the index. <br/>
     * <ul>
     * <li>if <code>nIndex</code> == 1, then we add a <b>WHERE</b></li>
     * <li>if <code>nIndex</code> != 1, then we add a <b>OR</b> or a <b>AND</b> depending of the wide search characteristic</li>
     * </ul>
     * 
     * @param bIsWideSearch
     *            true if it is a wide search, false otherwise
     * @param sbSQL
     *            the SQL query
     * @param nIndex
     *            the index
     * @return the new index
     */
    private int addSQLWhereOr( boolean bIsWideSearch, StringBuilder sbSQL, int nIndex )
    {
        if ( nIndex == 1 )
        {
            sbSQL.append( SQL_WHERE );
        }
        else
        {
            sbSQL.append( bIsWideSearch ? SQL_OR : SQL_AND );
        }

        return nIndex + 1;
    }

    /**
     * Builds the sql query.
     *
     * @param strSQL
     *            the str sql
     * @return the string
     */
    public String buildSQLQuery( String strSQL )
    {
        StringBuilder sbSQL = new StringBuilder( strSQL );
        int nIndex = 1;

        nIndex = buildFilter( sbSQL, containsIdFavorite( ), SQL_FILTER_ID_FAVORITE, nIndex );
        nIndex = buildFilter( sbSQL, containsIdExtendableResource( ), SQL_FILTER_ID_RESOURCE, nIndex );
        buildFilter( sbSQL, containsExtendableResourceType( ), SQL_FILTER_RESOURCE_TYPE, nIndex );

        return sbSQL.toString( );
    }

    /**
     * Sets the filter values.
     *
     * @param daoUtil
     *            the new filter values
     */
    public void setFilterValues( DAOUtil daoUtil )
    {
        int nIndex = 1;

        if ( containsIdFavorite( ) )
        {
            daoUtil.setInt( nIndex++, getIdFavorite( ) );
        }

        if ( containsIdExtendableResource( ) )
        {
            daoUtil.setString( nIndex++, getIdExtendableResource( ) );
        }

        if ( containsExtendableResourceType( ) )
        {
            daoUtil.setString( nIndex++, getExtendableResourceType( ) );
        }
    }
}

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

import javax.validation.constraints.NotNull;

/**
 *
 * Favorite
 *
 */
public class Favorite
{
    @NotNull
    private int _nIdFavorite;
    @NotNull
    private String _strIdExtendableResource;
    @NotNull
    private String _strExtendableResourceType;
    @NotNull
    private int _nFavoriteCount;

    /**
     * @return the nIdFavorite
     */
    public int getIdFavorite( )
    {
        return _nIdFavorite;
    }

    /**
     * @param nIdFavorite
     *            the nIdFavorite to set
     */
    public void setIdFavorite( int nIdFavorite )
    {
        _nIdFavorite = nIdFavorite;
    }

    /**
     * @return the strIdExtendableResource
     */
    public String getIdExtendableResource( )
    {
        return _strIdExtendableResource;
    }

    /**
     * @param strIdExtendableResource
     *            the strIdExtendableResource to set
     */
    public void setIdExtendableResource( String strIdExtendableResource )
    {
        _strIdExtendableResource = strIdExtendableResource;
    }

    /**
     * @return the extendableResourceType
     */
    public String getExtendableResourceType( )
    {
        return _strExtendableResourceType;
    }

    /**
     * @param strExtendableResourceType
     *            the extendableResourceType to set
     */
    public void setExtendableResourceType( String strExtendableResourceType )
    {
        _strExtendableResourceType = strExtendableResourceType;
    }

    /**
     * @return the nfavoriteCount
     */
    public int getFavoriteCount( )
    {
        return _nFavoriteCount;
    }

    /**
     * @param nfavoriteCount
     *            the nfavoriteCount to set
     */
    public void setFavoriteCount( int nfavoriteCount )
    {
        _nFavoriteCount = nfavoriteCount;
    }
}

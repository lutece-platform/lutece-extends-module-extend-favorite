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
package fr.paris.lutece.plugins.extend.modules.favorite.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;

/**
 * ICommentDAO.
 */
public interface IFavoriteDAO
{
    /**
     * Delete.
     *
     * @param nIdFavorite
     *            the n id Favorite
     * @param plugin
     *            the plugin
     */
    void delete( int nIdFavorite, Plugin plugin );

    /**
     * Delete by id hub resource.
     *
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     * @param plugin
     *            the plugin
     */
    void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     * Insert.
     *
     * @param favorite
     *            the Favorite
     * @param plugin
     *            the plugin
     */
    void insert( Favorite favorite, Plugin plugin );

    /**
     * Load.
     *
     * @param nIdFavorite
     *            the n id Favorite
     * @param plugin
     *            the plugin
     * @return the Favorite
     */
    Favorite load( int nIdFavorite, Plugin plugin );

    /**
     * Store.
     *
     * @param favorite
     *            the Favorite
     * @param plugin
     *            the plugin
     */
    void store( Favorite favorite, Plugin plugin );

    /**
     * Select by id hub resource.
     *
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     * @param plugin
     *            the plugin
     * @return the Favorite
     */
    Favorite loadByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     *
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return List Favorite
     */
    List<Favorite> loadByFilter( FavoriteFilter filter, Plugin plugin );
}

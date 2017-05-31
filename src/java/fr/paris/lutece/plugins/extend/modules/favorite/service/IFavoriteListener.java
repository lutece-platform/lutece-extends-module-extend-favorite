package fr.paris.lutece.plugins.extend.modules.favorite.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceUser;

/**
 * 
 * IFavoriteListener
 *
 */
public interface IFavoriteListener
{

    /**
     * Notify new favorite
     * 
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param request
     *            httpServletRequest
     */
    void favorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request );

    /**
     * Cancel Notify new favorite
     * 
     * @param strExtendableResourceType
     *            the extendable resource type
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param request
     *            httpServletRequest
     */
    void cancelFavorite( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request );

    /**
     * Can Favorite
     * 
     * @param startDate
     *            the start date
     * @param endDate
     *            the end date
     * @return boolean if can Favorite
     */
    boolean canFavorite( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user );
}

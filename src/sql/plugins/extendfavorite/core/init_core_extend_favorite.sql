-- liquibase formatted sql
-- changeset extendfavorite:init_core_extend_favorite.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_template WHERE template_name = 'extend_favorite'
--
-- Init core_template
--

INSERT INTO core_template ( template_name, template_value ) VALUES ('extend_favorite','\r\n<#if !favoriteClosed>	<#if canDeleteFavorite>	<div>		<p> #i18n{module.extend.favorite.extender.closed.label} : 		<a href="jsp/site/plugins/extend/modules/favorite/DoCancelFavorite.jsp?idExtendableResource=${idExtendableResource!}&extendableResourceType=${extendableResourceType!}" > #i18n{module.extend.favorite.extender.cancelFavorite.label}</a>		</p>	</div>	</#if>	<#if show == "all" || show == "actionFavorite">		<#if canFavorite && !canDeleteFavorite >		<div>			<span><a href="jsp/site/plugins/extend/modules/favorite/DoFavorite.jsp?idExtendableResource=${idExtendableResource!}&extendableResourceType=${extendableResourceType!}&favoriteValue=1"> #i18n{module.extend.favorite.extender.doFavorite.label}</a></span>		</div>		</#if>	</#if>\r\n</#if><#if favorite??><p> #i18n{module.extend.favorite.extender.count.label} :  ${favorite.favoriteCount} </p></#if>');

-- changeset extendfavorite:init_core_extend_favorite-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM core_template WHERE template_name = 'extend_favorite' AND template_value LIKE '%modules/favorite/Do%Favorite.jsp%'
UPDATE core_template SET template_value = '<div class="extend-favorite d-flex flex-column align-items-start gap-2 mb-3">
<#if !favoriteClosed>
<#if canDeleteFavorite>
<@cForm action="jsp/site/Portal.jsp?page=extendfavorite&amp;action=doCancelFavorite" class="extend-favorite-cancel">
<@cText>#i18n{module.extend.favorite.extender.closed.label}</@cText>
<@cInput type="hidden" name="idExtendableResource" id="favorite-cancel-idExtendableResource-${extendableResourceType!}-${idExtendableResource!}" value="${idExtendableResource!}" />
<@cInput type="hidden" name="extendableResourceType" id="favorite-cancel-extendableResourceType-${extendableResourceType!}-${idExtendableResource!}" value="${extendableResourceType!}" />
<@cInput type="hidden" name="_csrftoken" id="favorite-cancel-_csrftoken-${extendableResourceType!}-${idExtendableResource!}" value="${cancelFavoriteToken!}" />
<@cBtn label="#i18n{module.extend.favorite.extender.cancelFavorite.label}" class="secondary" />
</@cForm>
</#if>
<#if ( show == "all" || show == "actionFavorite" ) && canFavorite && !canDeleteFavorite>
<@cForm action="jsp/site/Portal.jsp?page=extendfavorite&amp;action=doFavorite" class="extend-favorite-add">
<@cInput type="hidden" name="idExtendableResource" id="favorite-add-idExtendableResource-${extendableResourceType!}-${idExtendableResource!}" value="${idExtendableResource!}" />
<@cInput type="hidden" name="extendableResourceType" id="favorite-add-extendableResourceType-${extendableResourceType!}-${idExtendableResource!}" value="${extendableResourceType!}" />
<@cInput type="hidden" name="favoriteValue" id="favorite-add-favoriteValue-${extendableResourceType!}-${idExtendableResource!}" value="1" />
<@cInput type="hidden" name="_csrftoken" id="favorite-add-_csrftoken-${extendableResourceType!}-${idExtendableResource!}" value="${favoriteToken!}" />
<@cBtn label="#i18n{module.extend.favorite.extender.doFavorite.label}" />
</@cForm>
</#if>
</#if>
<#if favorite??><@cText>#i18n{module.extend.favorite.extender.count.label} : ${favorite.favoriteCount}</@cText></#if>
</div>
' WHERE template_name = 'extend_favorite' AND template_value LIKE '%modules/favorite/Do%Favorite.jsp%';

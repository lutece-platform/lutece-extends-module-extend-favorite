-- liquibase formatted sql
-- changeset extendfavorite:update_db_extend_favorite-1.1.0-2.0.0.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'extend_favorite' AND column_name = 'id_favorite' AND extra NOT LIKE '%auto_increment%'
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
ALTER TABLE extend_favorite MODIFY COLUMN id_favorite INT AUTO_INCREMENT;

-- changeset extendfavorite:update_db_extend_favorite-1.1.0-2.0.0-rev1.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'extend_favorite_history' AND column_name = 'id_favorite_history' AND extra NOT LIKE '%auto_increment%'
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
ALTER TABLE extend_favorite_history MODIFY COLUMN id_favorite_history INT AUTO_INCREMENT;

-- changeset extendfavorite:update_db_extend_favorite-1.1.0-2.0.0-rev2.sql
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

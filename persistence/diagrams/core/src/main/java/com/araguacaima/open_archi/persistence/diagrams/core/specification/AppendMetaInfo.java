package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.persistence.diagrams.meta.History;
import com.araguacaima.open_archi.persistence.diagrams.meta.MetaInfo;
import com.araguacaima.open_archi.persistence.diagrams.meta.Version;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.specification.AbstractSpecification;

import java.util.*;

@SuppressWarnings("unchecked")
public class AppendMetaInfo extends AbstractSpecification {

    private static final Map<String, Object> versionParam = new HashMap<String, Object>() {{
        put("version", new Version());
    }};

    public AppendMetaInfo() {
        this(false);
    }

    public AppendMetaInfo(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        if (BaseEntity.class.isAssignableFrom(object.getClass())) {
            BaseEntity entity = (BaseEntity) object;
            MetaInfo meta;
            Date thisTime = Calendar.getInstance().getTime();
            if (entity.getMeta() == null) {
                if (map.get("meta") == null) {
                    MetaInfo storedMetaInfo = OrpheusDbJPAEntityManagerUtils.findByQuery(MetaInfo.class, MetaInfo.GET_META_INFO_BY_VERSION, versionParam);
                    if (storedMetaInfo == null) {
                        meta = new MetaInfo();
                        meta.addNewHistory(thisTime);
                        meta.setCreated(thisTime);
                        map.put("meta", meta);
                    } else {
                        meta = storedMetaInfo;
                    }
                    map.put("meta", meta);
                } else {
                    meta = (MetaInfo) map.get("meta");
                }
                entity.setMeta(meta);
            } else {
                meta = entity.getMeta();
                meta.addNewHistory(thisTime);
                map.put("meta", meta);
            }
            Account account = (Account) map.get("account");
            if (account != null) {
                Account createdBy = meta.getCreatedBy();
                if (createdBy == null) {
                    meta.setCreatedBy(account);
                } else {
                    History activeHistory = meta.getActiveHistory();
                    Account modifiedBy = activeHistory.getModifiedBy();
                    if (modifiedBy == null) {
                        activeHistory.setModifiedBy(account);
                    } else {
                        meta.addNewHistory(Calendar.getInstance().getTime(), account);
                    }
                }
            }
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}

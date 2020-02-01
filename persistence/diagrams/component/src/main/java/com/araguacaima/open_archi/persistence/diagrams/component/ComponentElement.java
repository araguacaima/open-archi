package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.DiagramableElement;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The word "component" is a hugely overloaded term in the system development
 * industry, but in this context a component is simply a grouping of related
 * functionality encapsulated behind a well-defined interface. If you're using a
 * language like Java or C#, the simplest way to think of a component is that
 * it's a collection of implementation classes behind an interface. Aspects such
 * as how those components are packaged (e.g. one component vs many components
 * per JAR file, DLL, shared library, etc) is a separate and orthogonal concern.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "ComponentModelElement")
public class ComponentElement extends ComponentLeafStaticElement implements DiagramableElement<ComponentElement> {

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(ComponentElement::getKind)
                .thenComparing(ComponentElement::getName)
                .compare(this, (ComponentElement) o);
    }

}
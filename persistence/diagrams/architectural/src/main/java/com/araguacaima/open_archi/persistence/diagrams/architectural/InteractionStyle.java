package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.Relationship;

/**
 * Use {@link InteractionStyle}s on {@link Relationship}s to make the difference between synchronous and asynchronous communication
 * visible.
 * to define different styles for synchronous and asynchronous communication.
 */
public enum InteractionStyle {

    /**
     * Denotes synchronous communication. The tag {@link InteractionStyle#SYNCHRONOUS} is automatically added to such {@link Relationship}s,
     * so you might use that tag to adapt the relationship style in the diagram
     */
    SYNCHRONOUS,

    /**
     * Denotes asynchronous communication. The tag {@link InteractionStyle#ASYNCHRONOUS} is automatically added to such {@link Relationship}s,
     * so you might use that tag to adapt the relationship style in the diagram
     */
    ASYNCHRONOUS

}
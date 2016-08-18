package components;

import java.util.List;

/**
 * A manager of entities.
 * 
 * @author <a href="http://www.cs.bsu.edu/~pvg">Paul Gestwicki</a>
 * @author Andrew DePersio (removeComponent() )
 * 
 */
public interface EntityManager {

    /**
     * Create a new entity. This entity is registered with this manager and
     * returned.
     * 
     * @return the created entity
     */
    public abstract Entity createEntity();
    
    public abstract void clear();

    /**
     * Add the given component to the given entity.
     * 
     * @param <T>
     *            the type of the component being added
     * @param e
     *            the entity to be modified, which must already be known by this
     *            entity manager.
     * @param component
     *            the component to add to <code>e</code>
     * @return the entity to which a component is added
     */
    public abstract <T extends Component> Entity addComponent(Entity e,
	    T component);

    /**
     * Remove the given component from the given entity.
     * 
     * @param <T>
     *            the type of the component being removed
     * @param e
     *            the entity to be modified, which must already be known by this
     *            entity manager.
     * @param component
     *            the component to remove to <code>e</code>
     * @return the entity to which a component is removed
     */
    public abstract <T extends Component> Entity removeComponent(Entity e,
	    T component);

    /**
     * Get the component of the given type from the given entity.
     * 
     * @param <T>
     *            the type of component being sought by this query
     * @param e
     *            an entity in the system
     * @param type
     *            the type of component
     * @return component of that entity
     * @throws NoSuchComponentException
     *             if the entity does not have such a component
     */
    public abstract <T extends Component> T getComponent(Entity e, Class<T> type);

    /**
     * Test whether an entity has a component of the specified type.
     * 
     * @param <T>
     *            the type of component for this query
     * @param e
     *            entity to test
     * @param type
     *            the type of component for the query
     * @return true if the entity has the component or false otherwise
     */
    public <T extends Component> boolean hasComponent(Entity e, Class<T> type);

    /**
     * Get all the entities that have the given component.
     * 
     * @param <T>
     *            the component type being requested
     * @param type
     *            the type of component requested
     * @return list of entities that have that component
     */
    public <T extends Component> List<Entity> getAll(Class<T> type);

    /**
     * Get all the entities that possess all of the given component types. Each
     * entity that is returned has each of the components (intersection (not
     * union)).
     * 
     * @param types
     *            the component types
     * @return list of entities possessing all of the given components
     */
    public List<Entity> getAll(Class<? extends Component>... types);

    /**
     * Remove an entity, and recursively all of its constituent components, from
     * this entity manager.
     * 
     * @param entity
     *            the entity to remove
     */
    public abstract void remove(Entity entity);
}
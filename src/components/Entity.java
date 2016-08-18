package components;

/**
 * An entity within the entity system.
 * 
 * @author <a href="http://www.cs.bsu.edu/~pvg">Paul Gestwicki</a>
 * @author Andrew DePersio (remove() )
 * @author David Rickey (hasAll())
 * 
 */
public final class Entity {

    /** A counter for the next available ID. */
    private static volatile int nextID = 0;

    /** The ID for this entity. This is used to determine uniqueness. */
    private final int id;

    /** The manager that is handling this entity. */
    private EntityManager manager;

    public Entity(EntityManager manager) {
	if (manager == null)
	    throw new IllegalArgumentException("EntityManager may not be null");
	this.manager = manager;
	this.id = nextID++;
    }

    /**
     * Return a component of this entity that matches the given type.
     * 
     * @param <T>
     *            component type
     * @param type
     *            component type
     * @return this entity as the given component type
     * @throws NoSuchComponentException
     *             if the entity does not have a component of the indicated type
     * @see EntityManager#getComponent(Entity, Class)
     */
    public <T extends Component> T as(Class<T> type) {
	return manager.getComponent(this, type);
    }

    /**
     * Add the given component to this entity.
     * 
     * @param component the component to add
     * @see EntityManager#addComponent(Entity, Component)
     * @return this entity
     */
    public Entity add(Component component) {
	manager.addComponent(this, component);
	return this;
    }
    
    /**
     * Remove the given component to this entity.
     * 
     * @param component the component to remove
     * @see EntityManager#addComponent(Entity, Component)
     * @return this entity
     */
    public Entity remove(Component component) {
	manager.removeComponent(this, component);
	return this;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (obj == this) {
	    return true;
	}
	if (obj.getClass() != getClass()) {
	    return false;
	}
	Entity rhs = (Entity) obj;
	return id == rhs.id;

    }
    
    /**
     * Test if this entity has a component of the specified type.
     * @param <T> component type
     * @param type component type
     * @return true if it does, false if it doesn't
     * @see EntityManager#hasComponent(Entity, Class)
     */
    public <T extends Component> boolean has(Class<T> type) {
	return manager.hasComponent(this, type);
    }
    
    public <T extends Component> boolean hasAll(Class<T> ... types){
	if (types.length == 0)
	    return false;
	for (Class<? extends Component> c : types){
	    if (!has(c))
		return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
		return new Integer(id+100).hashCode();
    }

    @Override
    public String toString() {
	return "id"+id;
    }
}

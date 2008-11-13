package de.lmu.ifi.dbs.elki.data.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.DatabaseObjectGroup;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriteable;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriterStream;

/**
 * Generic cluster class, that may or not have hierarchical information.
 * Note that every cluster MUST have a DatabaseObjectGroup, since it implements
 * the interface, too. Calls to the interface are proxied to the inner group object.
 * 
 * A hierarchy object of class SimpleHierarchy will be created automatically when
 * a list of parents and children is provided. Alternatively, a pre-existing
 * hierarchy object can be provided, e.g. when there is a single hierarchy object used
 * for keeping all the hierarchy information in one object.
 * 
 * @author Erich Schubert
 */
// TODO: disallow clusters without a DatabaseObjectGroup?
// TODO: remove the DatabaseObjectGroup interface to avoid confusion?
// TODO: add Model interface and delegations consequently since we have the group delegators?
// TODO: split TextWriteable interface into data writing and metadata writing? 
public abstract class BaseCluster<C extends BaseCluster<C,M>, M extends Model> implements HierarchyInterface<C>, DatabaseObject, DatabaseObjectGroup, TextWriteable {
  /**
   * To allow storage in a database, we need to keep an ID.
   */
  private int id;
  
  /**
   * Object that the hierarchy management is delegated to.
   */
  private HierarchyImplementation<C> hierarchy = null;

  /**
   * Cluster name.
   */
  protected String name = null;

  /**
   * Cluster data.
   */
  private DatabaseObjectGroup group = null;

  /**
   * Cluster model.
   */
  private M model = null;

  /**
   * Full constructor
   * 
   * @param name Cluster name. May be null.
   * @param group Group data
   * @param model Model. May be null.
   * @param hierarchy Hierarchy object. May be null.
   */
  public BaseCluster(String name, DatabaseObjectGroup group, M model, HierarchyImplementation<C> hierarchy) {
    super();
    // TODO: any way to check that this is a C? (see asC() method)
    // TODO: allow group == null? Throw exception otherwise?
    //assert(group != null);
    this.name = name;
    this.group = group;
    this.model = model;
    this.hierarchy = hierarchy;
  }

  /**
   * Constructor with hierarchy information.
   * A new FullHierarchy object will be created to store the hierarchy information.
   * 
   * @param name Cluster name. May be null.
   * @param group Group data
   * @param model Model. May be null.
   * @param children Children. Will NOT be copied.
   * @param parents Parents. Will NOT be copied.
   */
  public BaseCluster(String name, DatabaseObjectGroup group, M model, List<C> children, List<C> parents) {
    this(name, group, model, null);
    this.setHierarchy(new SimpleHierarchy<C>(this.asC(), children, parents));
  }

  /**
   * Constructor without hierarchy information. 
   * 
   * @param name
   * @param group
   */
  public BaseCluster(String name, DatabaseObjectGroup group, M model) {
    this(name, group, model, null);
  }

  /**
   * Constructor without hierarchy information and name 
   * 
   * @param group
   */
  public BaseCluster(DatabaseObjectGroup group, M model) {
    this(null, group, model, null);
  }

  /**
   * Constructor without hierarchy information and model 
   * 
   * @param group
   */
  public BaseCluster(String name, DatabaseObjectGroup group) {
    this(name, group, null, null);
  }

  /**
   * Constructor without hierarchy information and name and model 
   * 
   * @param group
   */
  public BaseCluster(DatabaseObjectGroup group) {
    this(null, group, null, null);
  }
  
  /**
   * Unchecked cast.
   * @return this, casted to <C>
   */
  @SuppressWarnings("unchecked")
  private final C asC() {
    // TODO: any chance of testing this? In constructor maybe?
    return (C) this;
  }

  /**
   * Test hierarchy
   */
  @Override
  public final boolean isHierarchical() {
    if (hierarchy == null) {
      return false;
    }
    return hierarchy.isHierarchical();
  }
  
  /**
   * Delegate to hierarchy object
   */
  @Override
  public int numChildren() {
    if (hierarchy == null) {
      return 0;
    }
    return hierarchy.numChildren(this.asC());
  }

  /**
   * Delegate to hierarchy object
   */
  @Override
  public List<C> getChildren() {
    if (hierarchy == null) {
      return null;
    }
    return hierarchy.getChildren(this.asC());
  }

  /**
   * Delegate to hierarchy object
   */
  @Override
  public <T extends Collection<C>> T getDescendants(T collection) {
    if (hierarchy == null) {
      return collection;
    }
    return hierarchy.getDescendants(this.asC(), collection);
  }
  
  /**
   * Collect descendants
   */
  public Set<C> getDescendants() {
    return getDescendants(new HashSet<C>());
  }

  /**
   * Delegate to hierarchy object
   */
  @Override
  public int numParents() {
    if (hierarchy == null) {
      return 0;
    }
    return hierarchy.numParents(this.asC());
  }
  
  /**
   * Delegate to hierarchy object
   */
  @Override
  public List<C> getParents() {
    if (hierarchy == null) {
      return null;
    }
    return hierarchy.getParents(this.asC());
  }

  /**
   * Delegate to hierarchy object
   */
  @Override
  public <T extends Collection<C>> T getAncestors(T collection) {
    if (hierarchy == null) {
      return collection;
    }
    return hierarchy.getAncestors(this.asC(), collection);
  }
  
  /**
   * Delegate to group.
   * 
   * @return
   */
  public int size() {
    return group.size();
  }

  /**
   * Delegate to group.
   */
  @Override
  public <O extends DatabaseObject> Database<O> getDatabase() {
    return group.getDatabase();
  }

  /**
   * Delegate to group.
   */
  @Override
  public Collection<Integer> getIDs() {
    return group.getIDs();
  }

  /**
   * Delegate to group.
   */
  @Override
  public Iterator<Integer> iterator() {
    return group.iterator();
  }
  
  /**
   * Get hierarchy object
   * 
   * @return hierarchy object
   */
  public HierarchyImplementation<C> getHierarchy() {
    return hierarchy;
  }

  /**
   * Set hierarchy object
   * 
   * @param hierarchy new hierarchy object
   */
  public void setHierarchy(HierarchyImplementation<C> hierarchy) {
    this.hierarchy = hierarchy;
  }
  
  /**
   * Get suggested label.
   */
  public String getSuggestedLabel() {
    if (getModel() != null) {
      return getModel().getSuggestedLabel();
    }
    // TODO: return null instead?
    return "Cluster";
  }
  
  /**
   * Return either the assigned name or the suggested label
   * 
   * @return a name for the cluster
   */
  public String getNameOrLabel() {
    if (name != null) return name;
    return getSuggestedLabel();
  }

  /**
   * Get Cluster name. May be null.
   * 
   * @return cluster name, or null
   */
  public String getName() {
    return name;
  }

  /**
   * Set Cluster name
   * 
   * @param name new cluster name
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Access group object
   * 
   * @return
   */
  public DatabaseObjectGroup getGroup() {
    return group;
  }
  
  /**
   * Access group object
   * 
   * @return
   */
  public void setGroup(DatabaseObjectGroup g) {
    group = g;
  }
  
  /**
   * Access group object
   * 
   * @return
   */
  @Deprecated
  public DatabaseObjectGroup getObjectGroup() {
    return group;
  }
  
  /**
   * Access group object
   * 
   * @return
   */
  @Deprecated
  public void setObjectGroup(DatabaseObjectGroup g) {
    group = g;
  }
  
  /**
   * Access model object
   * 
   * @return
   */
  public M getModel() {
    return model;
  }

  /**
   * Access model object
   * 
   * @param model
   */
  public void setModel(M model) {
    this.model = model;
  }

  /**
   * Write to a textual representation.
   * Writing the actual group data will be handled by the caller, this
   * is only meant to write the meta information.
   *  
   * @param out
   */
  public void writeToText(TextWriterStream out) {
    String name = getNameOrLabel();
    if (name != null) {
      out.commentPrintLn("Cluster: "+name);
    }
    out.commentPrintLn("Class: "+this.getClass().getName());
    out.commentPrintLn("Size: "+group.size());
    // print hierarchy information.
    if (isHierarchical()) {
      out.commentPrint("Parents: ");
      for (int i = 0; i < numParents(); i++) {
        if (i > 0) {
          out.commentPrint(", ");
        }
        out.commentPrint(getParents().get(i).getNameOrLabel());
      }
      out.commentPrintLn();
      out.commentPrint("Children: ");
      for (int i = 0; i < numChildren(); i++) {
        if (i > 0) {
          out.commentPrint(", ");
        }
        out.commentPrint(getChildren().get(i).getNameOrLabel());
      }
      out.commentPrintLn();
    }
    // also print model, if any and printable
    if (getModel() != null && getModel() instanceof TextWriteable) {
      ((TextWriteable)getModel()).writeToText(out);
    }
  }

  /**
   * Implementation of ID storage for DatabaseObject interface
   */
  @Override
  public Integer getID() {
    return id;
  }

  /**
   * Implementation of ID storage for DatabaseObject interface
   */
  @Override
  public void setID(Integer id) {
    this.id = id;
  }
}

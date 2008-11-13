package de.lmu.ifi.dbs.elki.result;

import java.util.ArrayList;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.utilities.pairs.SimplePair;

/**
 * Class representing Annotations backed by a database using {@link AssociationID}s.
 * 
 * @author Erich Schubert
 *
 * @param <O> Database object type
 */
// TODO: replace Object with a generics type?
public class AnnotationsFromDatabase<O extends DatabaseObject, T> implements AnnotationResult<T> {
  /**
   * database storage
   */
  private Database<O> database;

  /**
   * Storage of included associations.
   */
  private ArrayList<SimplePair<String, AssociationID<T>>> associations = new ArrayList<SimplePair<String, AssociationID<T>>>();

  /**
   * Constructor
   * 
   * @param db
   */
  public AnnotationsFromDatabase(Database<O> db) {
    this.database = db;
  }

  /**
   * DB accessor
   * 
   * @return
   */
  public Database<O> getDatabase() {
    return this.database;
  }

  /**
   * Add an association to the result.
   * 
   * @param label Label of the Annotation
   * @param association Association ID.
   */
  public void addAssociation(String label, AssociationID<T> association) {
    associations.add(new SimplePair<String, AssociationID<T>>(label, association));
  }

  /**
   * Add an annotation to the result for generics.
   * Note that type-safety is ignored here!
   * 
   * @param label Label of the Annotation
   * @param association Annotation data.
   */
  @SuppressWarnings("unchecked")
  public void addAssociationGenerics(String label, AssociationID<?> association) {
    associations.add(new SimplePair<String, AssociationID<T>>(label, (AssociationID<T>) association));
  }

  /**
   * Retrieve the given annotations for this object from the Database.
   */
  @Override
  public SimplePair<String,T>[] getAnnotations(Integer objID) {
    SimplePair<String,T>[] result = SimplePair.newArray(associations.size());
    int index = 0;
    for (SimplePair<String, AssociationID<T>> pair : associations) {
      T o = getDatabase().getAssociation(pair.getSecond(), objID);
      result[index] = new SimplePair<String, T>(pair.getFirst(), o);
      index++;
    }
    return result;
  }
}

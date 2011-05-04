package zephyr.plugin.jython.internal;

import org.python.core.PyList;

import zephyr.plugin.core.api.codeparser.parsers.AbstractCollectionParser;


public class ObjectPyListParser extends AbstractCollectionParser<PyList> {
  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof PyList))
      return false;
    PyList collection = (PyList) fieldValue;
    if (collection.size() == 0)
      return false;
    Object firstElement = collection.get(0);
    if (firstElement == null)
      return false;
    return !firstElement.getClass().isPrimitive();
  }

  @Override
  protected int nbChildren(PyList container) {
    return container.size();
  }

  @Override
  protected Object getElement(PyList container, int index) {
    return container.get(index);
  }
}

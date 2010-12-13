/**
 *
 *	LICENSE: see COPYING file
 *
**/

/*
 *    Saver.java
 *    Copyright (C) 2004 Stefan Mutter
 *
 */

package weka.gui.beans;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.DatabaseConverter;
import weka.core.converters.DatabaseSaver;


/**
 * Saves data sets using weka.core.converter classes
 * WARNING: This class was taken from the weka_src.jar and modified in order to put some more logs 
 *
 * @author <a href="mailto:mutter@cs.waikato.ac.nz">Stefan Mutter</a>
 * @version $Revision: 1.2 $
 *
 */
public class Saver extends AbstractDataSink implements WekaWrapper {

  /**
   * Holds the instances to be saved
   */
  private Instances m_dataSet;

  /**
   * Holds the structure
   */
  private Instances m_structure;
  
  

  /**
   * Global info for the wrapped loader (if it exists).
   */
  protected String m_globalInfo;

  /**
   * Thread for doing IO in
   */
  private transient SaveBatchThread m_ioThread;

  /**
   * Saver
   */
  private weka.core.converters.Saver m_Saver= new ArffSaver();

  /**
   * The relation name that becomes part of the file name
   */
  private String m_fileName;
  
  
  /** Flag indicating that instances will be saved to database. Used because structure information can only be sent after a database has been configured.*/
  private boolean m_isDBSaver;
  
  
 
  /**
   * Count for structure available messages
   */
  private int m_count;
  
  private int status = INITIALIZED;
  
  public static final int INITIALIZED = 0;
  public static final int EXECUTING = 1;
  public static final int TERMINATED = 2;

  private static transient Logger logger = Logger.getLogger(Saver.class);
  
  private class SaveBatchThread extends Thread {
    private DataSink m_DS;

    public SaveBatchThread(DataSink ds) {
      m_DS= ds;
    }

    public void run() {
    	logger.debug("IN");
    	try {
        m_visual.setAnimated();
        logger.debug("Setted Animated");
        m_Saver.setInstances(m_dataSet);
        logger.debug("Setted Dataset");
        m_Saver.writeBatch();
        logger.debug("Batch Written");
	
      } catch (Exception ex) {
    	  logger.error("SaveBatchThread.run", ex);
      } finally {
        block(false);       
        status = TERMINATED;
        m_visual.setStatic();
      }
      logger.debug("OUT");
    }
  }
  
  /**
   * Function used to stop code that calls acceptTrainingSet. This is 
   * needed as classifier construction is performed inside a separate
   * thread of execution.
   *
   * @param tf a <code>boolean</code> value
   */
  private synchronized void block(boolean tf) {

    if (tf) {
      try {
	if (m_ioThread.isAlive()) {
	  wait();
	  }
      } catch (InterruptedException ex) {
      }
    } else {
      notifyAll();
    }
  }
  
  /**
   * Wait until finish.
   */
  public void waitUntilFinish() {
	  logger.debug("IN");
	  logger.debug("Status begin: "+ status);
	  while(status != TERMINATED) {
		  try {
			    //logger.debug("Status Prima di Sleep: "+status);
		        Thread.currentThread().sleep(1000);
		        //logger.debug("Status Dopo di Sleep: "+status);
		  } catch (InterruptedException e) {
			  logger.debug(e);
			  e.printStackTrace();
		  }
		  
	  }	  
	  logger.debug("Status end: "+ status);
	  logger.debug("OUT");
  }
  
  /**
   * Get the custom (descriptive) name for this bean (if one has been set)
   * 
   * @return the custom name (or the default name)
   */
  public String getCustomName() {
    return m_visual.getText();
  }  
  
  /**
   * Set a custom (descriptive) name for this bean
   * 
   * @param name the name to use
   */
  public void setCustomName(String name) {
    m_visual.setText(name);
  }
  
  /**
   * Returns true if. at this time, the bean is busy with some
   * (i.e. perhaps a worker thread is performing some calculation).
   * 
   * @return true if the bean is busy.
   */
  public boolean isBusy() {
    return (m_ioThread != null);
  }

  /**
   * Global info (if it exists) for the wrapped loader.
   * 
   * @return the global info
   */
  public String globalInfo() {
    return m_globalInfo;
  }

  /**
   * Contsructor.
   */  
  public Saver() {
    super();
    setSaver(m_Saver);
    m_fileName = "";
    m_dataSet = null;
    m_count = 0;
    
  }

  

  /**
   * Set the loader to use.
   * 
   * @param saver a Saver
   */
  public void setSaver(weka.core.converters.Saver saver) {
    boolean loadImages = true;
    if (saver.getClass().getName().
	compareTo(m_Saver.getClass().getName()) == 0) {
      loadImages = false;
    }
    m_Saver = saver;
    String saverName = saver.getClass().toString();
    saverName = saverName.substring(saverName.
				      lastIndexOf('.')+1, 
				      saverName.length());
    if (loadImages) {

      if (!m_visual.loadIcons(BeanVisual.ICON_PATH+saverName+".gif",
			    BeanVisual.ICON_PATH+saverName+"_animated.gif")) {
	useDefaultVisual();
      }
    }
    m_visual.setText(saverName);

    
    // get global info
    m_globalInfo = KnowledgeFlowApp.getGlobalInfo(m_Saver);
    if(m_Saver instanceof DatabaseConverter)
        m_isDBSaver = true;
    else
        m_isDBSaver = false;
  }
  
  
  
  /**
   * Method reacts to a dataset event and starts the writing process in batch mode.
   * 
   * @param e a dataset event
   */  
  public synchronized void acceptDataSet(DataSetEvent e) {
  
      m_fileName = e.getDataSet().relationName();
      m_dataSet = e.getDataSet();
      if(e.isStructureOnly() && m_isDBSaver && ((DatabaseSaver)m_Saver).getRelationForTableName()){//
          ((DatabaseSaver)m_Saver).setTableName(m_fileName);
      }
      if(!e.isStructureOnly()){
          if(!m_isDBSaver){
            try{
                m_Saver.setDirAndPrefix(m_fileName,"");
            }catch (Exception ex){
                logger.error("acceptDataSet", ex);
            }
          }
          saveBatch();
      }
  }
  
  /**
   * Method reacts to a test set event and starts the writing process in batch mode.
   * 
   * @param e test set event
   */  
  public synchronized void acceptTestSet(TestSetEvent e) {
  
      m_fileName = e.getTestSet().relationName();
      m_dataSet = e.getTestSet();
      if(e.isStructureOnly() && m_isDBSaver && ((DatabaseSaver)m_Saver).getRelationForTableName()){
          ((DatabaseSaver)m_Saver).setTableName(m_fileName);
      }
      if(!e.isStructureOnly()){
          if(!m_isDBSaver){
            try{
                m_Saver.setDirAndPrefix(m_fileName,"_test_"+e.getSetNumber()+"_of_"+e.getMaxSetNumber());
            }catch (Exception ex){
            	logger.error("acceptTestSet", ex);
            }
          }
          else{
              String setName = ((DatabaseSaver)m_Saver).getTableName();
              setName = setName.replaceFirst("_[tT][eE][sS][tT]_[0-9]+_[oO][fF]_[0-9]+","");
              ((DatabaseSaver)m_Saver).setTableName(setName+"_test_"+e.getSetNumber()+"_of_"+e.getMaxSetNumber());
          }
          saveBatch();
      }
  }
  
  /**
   * Method reacts to a training set event and starts the writing process in batch
   * mode.
   * 
   * @param e a training set event
   */  
  public synchronized void acceptTrainingSet(TrainingSetEvent e) {
  
      m_fileName = e.getTrainingSet().relationName();
      m_dataSet = e.getTrainingSet();
      if(e.isStructureOnly() && m_isDBSaver && ((DatabaseSaver)m_Saver).getRelationForTableName()){
           ((DatabaseSaver)m_Saver).setTableName(m_fileName);
      }
      if(!e.isStructureOnly()){
          if(!m_isDBSaver){
            try{
                m_Saver.setDirAndPrefix(m_fileName,"_training_"+e.getSetNumber()+"_of_"+e.getMaxSetNumber());
            }catch (Exception ex){
                logger.error("acceptTrainingSet", ex);
            }
          }
          else{
              String setName = ((DatabaseSaver)m_Saver).getTableName();
              setName = setName.replaceFirst("_[tT][rR][aA][iI][nN][iI][nN][gG]_[0-9]+_[oO][fF]_[0-9]+","");
              ((DatabaseSaver)m_Saver).setTableName(setName+"_training_"+e.getSetNumber()+"_of_"+e.getMaxSetNumber());
          }
          saveBatch();
      }
  }
  
  /**
   * Saves instances in batch mode.
   */  
  public synchronized void saveBatch(){
  
      m_Saver.setRetrieval(m_Saver.BATCH);
      m_visual.setText(m_fileName);
      m_ioThread = new SaveBatchThread(Saver.this);
      m_ioThread.setPriority(Thread.MIN_PRIORITY);
      m_ioThread.start();
      block(true);
  }
  
  /**
   * Methods reacts to instance events and saves instances incrementally.
   * If the instance to save is null, the file is closed and the saving process is
   * ended.
   * 
   * @param e instance event
   */  
  public synchronized void acceptInstance(InstanceEvent e) {
      
      
      if(e.getStatus() == e.FORMAT_AVAILABLE){
        m_Saver.setRetrieval(m_Saver.INCREMENTAL);
        m_structure = e.getStructure();
        m_fileName = m_structure.relationName();
        m_Saver.setInstances(m_structure);
        if(m_isDBSaver)
            if(((DatabaseSaver)m_Saver).getRelationForTableName())
                ((DatabaseSaver)m_Saver).setTableName(m_fileName);
      }
      if(e.getStatus() == e.INSTANCE_AVAILABLE){
        m_visual.setAnimated();
        if(m_count == 0){
            if(!m_isDBSaver){
                try{
                    m_Saver.setDirAndPrefix(m_fileName,"");
                }catch (Exception ex){
                    logger.error("acceptInstance", ex);
                    m_visual.setStatic();
                }
            }
            m_count ++;
        }
        try{  
            m_visual.setText(m_fileName);
            m_Saver.writeIncremental(e.getInstance());
        } catch (Exception ex) {
            m_visual.setStatic();
            logger.error("acceptInstance", ex);
        }
      }
      if(e.getStatus() == e.BATCH_FINISHED){
        try{  
            m_Saver.writeIncremental(e.getInstance());
            m_Saver.writeIncremental(null);
            //m_firstNotice = true;
            m_visual.setStatic();
            m_count = 0;
        } catch (Exception ex) {
            m_visual.setStatic();
            logger.error("acceptInstance", ex);
        }
      }
  }
  
  

  /**
   * Get the saver.
   * 
   * @return a <code>weka.core.converters.Saver</code> value
   */
  public weka.core.converters.Saver getSaver() {
    return m_Saver;
  }

  /**
   * Set the saver.
   * 
   * @param algorithm a Saver
   */
  public void setWrappedAlgorithm(Object algorithm) 
    {

    if (!(algorithm instanceof weka.core.converters.Saver)) { 
      throw new IllegalArgumentException(algorithm.getClass()+" : incorrect "
					 +"type of algorithm (Loader)");
    }
    setSaver((weka.core.converters.Saver)algorithm);
  }

  /**
   * Get the saver.
   * 
   * @return a Saver
   */
  public Object getWrappedAlgorithm() {
    return getSaver();
  }

  /**
   * Stops the bean.
   */  
  public void stop() {
  }
  
  
  /**
   * The main method for testing.
   * 
   * @param args the args
   */  
  public static void main(String [] args) {
    try {
      final javax.swing.JFrame jf = new javax.swing.JFrame();
      jf.getContentPane().setLayout(new java.awt.BorderLayout());

      final Saver tv = new Saver();

      jf.getContentPane().add(tv, java.awt.BorderLayout.CENTER);
      jf.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent e) {
          jf.dispose();
          System.exit(0);
        }
      });
      jf.setSize(800,600);
      jf.setVisible(true);
    } catch (Exception ex) {
    	logger.error("main", ex);
    }
  }
  
}



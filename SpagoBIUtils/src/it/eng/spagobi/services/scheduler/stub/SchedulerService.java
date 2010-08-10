/**
 * SchedulerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.scheduler.stub;

public interface SchedulerService extends java.rmi.Remote {
    public java.lang.String getJobList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public java.lang.String getJobSchedulationList(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.String deleteSchedulation(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.String deleteJob(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.String defineJob(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public java.lang.String getJobDefinition(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.String scheduleJob(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public java.lang.String getJobSchedulationDefinition(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public java.lang.String existJobDefinition(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
}

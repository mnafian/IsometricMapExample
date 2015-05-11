package com.niffy.AndEngineLockStepEngine.options;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseOptions implements IBaseOptions {
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(BaseOptions.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected InetAddress mHostIP;
    protected int mBufferSize = 512;
    protected int mTCPServerPort = 9999;
    protected int mTCPClientPort = 9998;
    protected int mUDPPort = 9998;
    protected int mStepsBeforeCrisis = 0;
    protected long mStandardTickLength = 0;
    protected int mAckWindowSize = 0;
    protected HashMap<String, Integer> mMessagePoolOptions;
    protected int mVersion = -1;
    protected long mPingRTT = 0;
    protected String mClientName = "default";

    // ===========================================================
    // Constructors
    // ===========================================================

    public BaseOptions() {
        this.mMessagePoolOptions = new HashMap<String, Integer>();
    }

    public BaseOptions(final IBaseOptions pBaseOptions) {
        this();
        this.mHostIP = pBaseOptions.getHostIP();
        this.mTCPServerPort = pBaseOptions.getTCPServerPort();
        this.mTCPClientPort = pBaseOptions.getTCPClientPort();
        this.mUDPPort = pBaseOptions.getUDPPort();
        this.mStepsBeforeCrisis = pBaseOptions.getStepsBeforeCrisis();
        this.mStandardTickLength = pBaseOptions.getStandardTickLength();
        this.mAckWindowSize = pBaseOptions.getAckWindowSize();
        this.mVersion = pBaseOptions.getVersionNumber();
        this.mBufferSize = pBaseOptions.getNetworkBufferSize();
        this.mPingRTT = pBaseOptions.getPingRTT();
        this.mClientName = pBaseOptions.getClientName();
        Iterator<Entry<String, Integer>> entries = pBaseOptions.getMessagePoolProperties().entrySet().iterator();
        while (entries.hasNext()) {
            Entry<String, Integer> entry = entries.next();
            this.mMessagePoolOptions.put(entry.getKey(), entry.getValue());
        }
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public int getStepsBeforeCrisis() {
        return this.mStepsBeforeCrisis;
    }

    @Override
    public void setStepsBeforeCrisis(int pStepsBeforeCrisis) {
        this.mStepsBeforeCrisis = pStepsBeforeCrisis;
    }

    @Override
    public long getStandardTickLength() {
        return this.mStandardTickLength;
    }

    @Override
    public void setStandardTickLength(long pStepLength) {
        this.mStandardTickLength = pStepLength;
    }

    @Override
    public int getAckWindowSize() {
        return this.mAckWindowSize;
    }

    @Override
    public void setAckWindowSize(int pWindowSize) {
        this.mAckWindowSize = pWindowSize;
    }

    @Override
    public int getVersionNumber() {
        return this.mVersion;
    }

    @Override
    public void setVersionNumber(int pVersionNumber) {
        this.mVersion = pVersionNumber;
    }

    @Override
    public int getNetworkBufferSize() {
        return this.mBufferSize;
    }

    @Override
    public void setNetworkBufferSize(int pSize) {
        this.mBufferSize = pSize;
    }

    @Override
    public InetAddress getHostIP() {
        return this.mHostIP;
    }

    @Override
    public void setHostIP(InetAddress pHostIP) {
        this.mHostIP = pHostIP;
    }

    @Override
    public int getTCPServerPort() {
        return this.mTCPServerPort;
    }

    @Override
    public void setTCPServerPort(int pTCPPort) {
        this.mTCPServerPort = pTCPPort;
    }

    @Override
    public int getTCPClientPort() {
        return this.mTCPClientPort;
    }

    @Override
    public void setTCPClientPort(int pTCPPort) {
        this.mTCPClientPort = pTCPPort;
    }

    @Override
    public int getUDPPort() {
        return this.mUDPPort;
    }

    @Override
    public void setUDPPort(int pUDPPort) {
        this.mUDPPort = pUDPPort;
    }

    @Override
    public long getPingRTT() {
        return this.mPingRTT;
    }

    @Override
    public void setPingRTT(long pDuration) {
        this.mPingRTT = pDuration;
    }

    @Override
    public String getClientName() {
        return this.mClientName;
    }

    @Override
    public void setClientName(String pClientName) {
        this.mClientName = pClientName;
    }

    @Override
    public void addPoolProperties(String pTag, int pValue) {
        this.mMessagePoolOptions.put(pTag, pValue);
    }

    @Override
    public int getPoolProperties(String pTag) {
        if (this.mMessagePoolOptions.containsKey(pTag)) {
            return this.mMessagePoolOptions.get(pTag);
        } else {
            return -1;
        }
    }

    @Override
    public HashMap<String, Integer> getMessagePoolProperties() {
        return this.mMessagePoolOptions;
    }

    @Override
    public int getTCPPort() {
        return 0;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

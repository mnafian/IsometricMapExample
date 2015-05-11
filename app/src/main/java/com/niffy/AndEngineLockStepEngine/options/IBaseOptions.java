package com.niffy.AndEngineLockStepEngine.options;

import java.net.InetAddress;
import java.util.HashMap;

import com.niffy.AndEngineLockStepEngine.messages.pool.MessagePoolTags;

public interface IBaseOptions {
    public int getVersionNumber();

    public void setVersionNumber(final int pVersionNumber);

    public InetAddress getHostIP();

    public void setHostIP(final InetAddress pHostIP);

    public int getUDPPort();

    public void setUDPPort(final int pUDPPort);

    public int getTCPServerPort();

    public void setTCPServerPort(final int pTCPPort);

    public int getTCPClientPort();

    public void setTCPClientPort(final int pTCPPort);

    public int getAckWindowSize();

    public void setAckWindowSize(final int pWindowSize);

    public long getStandardTickLength();

    public void setStandardTickLength(final long pStepLength);

    public int getStepsBeforeCrisis();

    public void setStepsBeforeCrisis(final int pStepsBeforeCrisis);

    public int getNetworkBufferSize();

    public void setNetworkBufferSize(final int pSize);

    public long getPingRTT();

    public void setPingRTT(final long pDuration);

    public String getClientName();

    public void setClientName(final String pClientName);

    /**
     * Set a property for the a message for its pool size
     *
     * @param pTag   {@link String} of the tag from {@link MessagePoolTags}
     * @param pValue {@link Integer} value of the size
     */
    public void addPoolProperties(final String pTag, final int pValue);

    /**
     * Get a property for the a message for its pool size
     *
     * @param pTag {@link String} of the tag from {@link MessagePoolTags}
     * @return{@link Integer} value of the size or <code>null</code> if not
     * found
     */
    public int getPoolProperties(final String pTag);

    public HashMap<String, Integer> getMessagePoolProperties();

    int getTCPPort();
}

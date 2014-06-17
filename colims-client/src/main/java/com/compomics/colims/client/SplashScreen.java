package com.compomics.colims.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Peter De Bruycker
 */
public class SplashScreen implements ApplicationContextAware, BeanPostProcessor, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class);

    private JWindow window;
    private Image image;
    private final String imageResourcePath;
    private ApplicationContext context;
    private final JProgressBar progressBar;
    private int progress = 0;
    private int maximum;
    private final boolean showProgressLabel;

    /**
     *
     */
    public SplashScreen() {
        progressBar = new JProgressBar();
        imageResourcePath = "/icons/colims-splash.png";
        showProgressLabel = true;
    }

    /**
     *
     * @return
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * Show the splash screen.
     */
    public void splash() {
        window = new JWindow();

        if (image == null) {
            image = loadImage(imageResourcePath);
            if (image == null) {
                return;
            }
        }
        MediaTracker mediaTracker = new MediaTracker(window);
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while waiting for splash image to load.");
        }

        window.getContentPane().add(new JLabel(new ImageIcon(image)));
        window.getContentPane().add(progressBar, BorderLayout.SOUTH);
        window.pack();
        center(window);

        window.setVisible(true);
    }

    /**
     * Dispose of the the splash screen. Once disposed, the same splash screen
     * instance may not be shown again.
     */
    public void dispose() {
        window.dispose();
        window = null;
    }

    /**
     * @param context
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * @param bean
     * @param name
     * @return
     * @see
     * org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        progressBar.setValue(progress++);
        if (showProgressLabel) {
            progressBar.setString("Loading bean " + name + "...");
        }

        return bean;
    }

    /**
     * @param bean
     * @param name
     * @return
     * @see
     * org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    /**
     * @throws java.lang.Exception
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (showProgressLabel) {
            progressBar.setStringPainted(true);
            progressBar.setString("Loading context...");
        }
        progressBar.setMinimum(0);
        maximum = context.getBeanDefinitionCount();
        progressBar.setMaximum(maximum);
        splash();
    }

    /**
     * Set the progress message of the progress bar.
     *
     * @param progressMessage the progress bar label message
     * @param value the progress bar value
     */
    public void setProgressLabel(String progressMessage, int value) {
        if (showProgressLabel) {
            progressBar.setValue(progressBar.getMaximum() - 1);
            progressBar.setString(progressMessage);
        }
    }

    private void center(JWindow window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = window.getBounds();
        window.setLocation((screen.width - r.width) / 2, (screen.height - r.height) / 2);
    }

    private Image loadImage(String path) {
        URL url = this.getClass().getResource(path);
        
        if (url == null) {
            LOGGER.warn("Unable to locate splash screen in classpath at: " + path);
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
    }

}

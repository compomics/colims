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
import org.springframework.util.Assert;

/**
 * @author Peter De Bruycker
 */
public class SplashScreen implements ApplicationContextAware, BeanPostProcessor, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class);

    private JWindow window;
    private Image image;
    private String imageResourcePath;
    private ApplicationContext context;
    private JProgressBar progressBar;
    private int beanCount = 0;
    private int progress = 0;
    private boolean showProgressLabel;

    public SplashScreen() {
        progressBar = new JProgressBar();
        imageResourcePath = "/icons/colims_icon.png";
        showProgressLabel = true;
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

    public void setImageResourcePath(String path) {
        Assert.hasText(path, "The splash screen image resource path is required");
        this.imageResourcePath = path;
    }

    private void center(JWindow window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = window.getBounds();
        window.setLocation((screen.width - r.width) / 2, (screen.height - r.height) / 2);
    }

    /**
     * Dispose of the the splash screen. Once disposed, the same splash screen
     * instance may not be shown again.
     */
    public void dispose() {
        window.dispose();
        window = null;
    }

    private Image loadImage(String path) {
        URL url = this.getClass().getResource(path);
        if (url == null) {
            LOGGER.warn("Unable to locate splash screen in classpath at: " + path);
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
    }

    /**
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * @see
     * org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        progressBar.setValue(progress++);
        if (showProgressLabel) {
            progressBar.setString("Loading bean " + name);
        }

        return bean;
    }

    /**
     * @see
     * org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    /**
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (showProgressLabel) {
            progressBar.setStringPainted(true);
            progressBar.setString("Loading context");
        }
        progressBar.setMinimum(0);
        progressBar.setMaximum(context.getBeanDefinitionCount());
        splash();
    }

    public boolean getShowProgressLabel() {
        return showProgressLabel;
    }

    public void setShowProgressLabel(boolean showProgressLabel) {
        this.showProgressLabel = showProgressLabel;
    }
   
}

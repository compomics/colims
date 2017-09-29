package com.compomics.colims.client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter De Bruycker
 */
public class SplashScreen implements ApplicationContextAware, BeanPostProcessor, InitializingBean {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SplashScreen.class);

    /**
     * The splash screen window.
     */
    private JWindow window;
    /**
     * The splash screen image.
     */
    private Image image;
    /**
     * The image path.
     */
    private final String imageResourcePath;
    /**
     * The application context.
     */
    private ApplicationContext context;
    /**
     * The splash screen progress bar.
     */
    private final JProgressBar progressBar;
    /**
     * The progress bar's progress.
     */
    private int progress = 0;
    /**
     * The total number of beans.
     */
    private int maximum;
    /**
     * Show the progress label boolean.
     */
    private final boolean showProgressLabel;

    /**
     * No-arg Constructor.
     */
    public SplashScreen() {
        progressBar = new JProgressBar();
        imageResourcePath = "/icons/colims-splash.png";
        showProgressLabel = true;
    }

    /**
     * Return the number of beans.
     *
     * @return the number of beans
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
            try {
                image = loadImage(imageResourcePath);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
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
     * Dispose of the the splash screen. Once disposed, the same splash screen instance may not be shown again.
     */
    public void dispose() {
        window.dispose();
        window = null;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String name) {
        progressBar.setValue(progress++);
        if (showProgressLabel) {
            progressBar.setString("Loading bean " + name + "...");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String name) {
        return bean;
    }

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
     * @param value           the progress bar value
     */
    public void setProgressLabel(final String progressMessage, final int value) {
        if (showProgressLabel) {
            progressBar.setValue(progressBar.getMaximum() - 1);
            progressBar.setString(progressMessage);
        }
    }

    /**
     * Center the splash screen.
     *
     * @param window the splash screen window
     */
    private void center(final JWindow window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = window.getBounds();
        window.setLocation((screen.width - r.width) / 2, (screen.height - r.height) / 2);
    }

    /**
     * Load the image from the given path.
     *
     * @param path the image file path
     * @return the Image instance
     */
    private Image loadImage(final String path) throws IOException {
//        URL url = this.getClass().getResource(path);
        URL url = new ClassPathResource(path).getURL();

        if (url == null) {
            LOGGER.warn("Unable to locate splash screen in classpath at: " + path);
            return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
    }

}

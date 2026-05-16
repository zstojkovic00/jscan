package com.zeljko;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import javax.swing.UIManager;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class JFuzzyyy {
        private static final long serialVersionUID = -3470703747817429120L;
        public static final ProjectInfo INFO = new JFreeChartInfo();
        public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", 1, 18);
        public static final Paint DEFAULT_BACKGROUND_PAINT = UIManager.getColor("Panel.background");
        public static final Image DEFAULT_BACKGROUND_IMAGE = null;
        public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = 15;
        public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5F;
        private transient RenderingHints renderingHints;
        private boolean borderVisible;
        private transient Stroke borderStroke;
        private transient Paint borderPaint;
        private RectangleInsets padding;
        private TextTitle title;
        private java.util.List subtitles;
        private Plot plot;
        private transient Paint backgroundPaint;
        private transient Image backgroundImage;
        private int backgroundImageAlignment;
        private float backgroundImageAlpha;
        private transient EventListenerList changeListeners;
        private transient EventListenerList progressListeners;
        private boolean notify;

        public JFreeChart(Plot plot) {
            this((String)null, (Font)null, plot, true);
        }


        public JFreeChart(String title, Font titleFont, Plot plot, boolean createLegend) {
            this.backgroundImageAlignment = 15;
            this.backgroundImageAlpha = 0.5F;
            if (plot == null) {
                throw new NullPointerException("Null 'plot' argument.");
            } else {
                this.progressListeners = new EventListenerList();
                this.changeListeners = new EventListenerList();
                this.notify = true;
                this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                this.borderVisible = false;
                this.borderStroke = new BasicStroke(1.0F);
                this.borderPaint = Color.black;
                this.padding = RectangleInsets.ZERO_INSETS;
                this.plot = plot;
                plot.addChangeListener(this);
                this.subtitles = new ArrayList();
                if (createLegend) {
                    LegendTitle legend = new LegendTitle(this.plot);
                    legend.setMargin(new RectangleInsets((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F));
                    legend.setFrame(new LineBorder());
                    legend.setBackgroundPaint(Color.white);
                    legend.setPosition(RectangleEdge.BOTTOM);
                    this.subtitles.add(legend);
                    legend.addChangeListener(this);
                }

                if (title != null) {
                    if (titleFont == null) {
                        titleFont = DEFAULT_TITLE_FONT;
                    }

                    this.title = new TextTitle(title, titleFont);
                    this.title.addChangeListener(this);
                }

                this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
                this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
                this.backgroundImageAlignment = 15;
                this.backgroundImageAlpha = 0.5F;
            }
        }

        public RenderingHints getRenderingHints() {
            return this.renderingHints;
        }

        public void setRenderingHints(RenderingHints renderingHints) {
            if (renderingHints == null) {
                throw new NullPointerException("RenderingHints given are null");
            } else {
                this.renderingHints = renderingHints;
                this.fireChartChanged();
            }
        }

        public boolean isBorderVisible() {
            return this.borderVisible;
        }

        public void setBorderVisible(boolean visible) {
            this.borderVisible = visible;
            this.fireChartChanged();
        }

        public Stroke getBorderStroke() {
            return this.borderStroke;
        }

        public void setBorderStroke(Stroke stroke) {
            this.borderStroke = stroke;
            this.fireChartChanged();
        }

        public Paint getBorderPaint() {
            return this.borderPaint;
        }

        public void setBorderPaint(Paint paint) {
            this.borderPaint = paint;
            this.fireChartChanged();
        }

        public RectangleInsets getPadding() {
            return this.padding;
        }

        public void setPadding(RectangleInsets padding) {
            if (padding == null) {
                throw new IllegalArgumentException("Null 'padding' argument.");
            } else {
                this.padding = padding;
                this.notifyListeners(new ChartChangeEvent(this));
            }
        }

        public TextTitle getTitle() {
            return this.title;
        }

        public void setTitle(TextTitle title) {
            if (this.title != null) {
                this.title.removeChangeListener(this);
            }

            this.title = title;
            if (title != null) {
                title.addChangeListener(this);
            }

            this.fireChartChanged();
        }

        public void setTitle(String text) {
            if (text != null) {
                if (this.title == null) {
                    this.setTitle(new TextTitle(text, DEFAULT_TITLE_FONT));
                } else {
                    this.title.setText(text);
                }
            } else {
                this.setTitle((TextTitle)null);
            }

        }

        public void addLegend(LegendTitle legend) {
            this.addSubtitle(legend);
        }

        public LegendTitle getLegend() {
            return this.getLegend(0);
        }

        public LegendTitle getLegend(int index) {
            int seen = 0;

            for(Title subtitle : this.subtitles) {
                if (subtitle instanceof LegendTitle) {
                    if (seen == index) {
                        return (LegendTitle)subtitle;
                    }

                    ++seen;
                }
            }

            return null;
        }

        public void removeLegend() {
            this.removeSubtitle(this.getLegend());
        }

        public java.util.List getSubtitles() {
            return new ArrayList(this.subtitles);
        }

        public void setSubtitles(List subtitles) {
            if (subtitles == null) {
                throw new NullPointerException("Null 'subtitles' argument.");
            } else {
                this.setNotify(false);
                this.clearSubtitles();

                for(Title t : subtitles) {
                    if (t != null) {
                        this.addSubtitle(t);
                    }
                }

                this.setNotify(true);
            }
        }

        public int getSubtitleCount() {
            return this.subtitles.size();
        }

        public Title getSubtitle(int index) {
            if (index >= 0 && index < this.getSubtitleCount()) {
                return (Title)this.subtitles.get(index);
            } else {
                throw new IllegalArgumentException("Index out of range.");
            }
        }

        public void addSubtitle(Title subtitle) {
            if (subtitle == null) {
                throw new IllegalArgumentException("Null 'subtitle' argument.");
            } else {
                this.subtitles.add(subtitle);
                subtitle.addChangeListener(this);
                this.fireChartChanged();
            }
        }

        public void addSubtitle(int index, Title subtitle) {
            if (index >= 0 && index <= this.getSubtitleCount()) {
                if (subtitle == null) {
                    throw new IllegalArgumentException("Null 'subtitle' argument.");
                } else {
                    this.subtitles.add(index, subtitle);
                    subtitle.addChangeListener(this);
                    this.fireChartChanged();
                }
            } else {
                throw new IllegalArgumentException("The 'index' argument is out of range.");
            }
        }

        public void clearSubtitles() {
            for(Title t : this.subtitles) {
                t.removeChangeListener(this);
            }

            this.subtitles.clear();
            this.fireChartChanged();
        }

        public void removeSubtitle(Title title) {
            this.subtitles.remove(title);
            this.fireChartChanged();
        }

        public Plot getPlot() {
            return this.plot;
        }

        public CategoryPlot getCategoryPlot() {
            return (CategoryPlot)this.plot;
        }

        public XYPlot getXYPlot() {
            return (XYPlot)this.plot;
        }

        public boolean getAntiAlias() {
            Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
            return RenderingHints.VALUE_ANTIALIAS_ON.equals(val);
        }

        public void setAntiAlias(boolean flag) {
            Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
            if (val == null) {
                val = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
            }

            if ((flag || !RenderingHints.VALUE_ANTIALIAS_OFF.equals(val)) && (!flag || !RenderingHints.VALUE_ANTIALIAS_ON.equals(val))) {
                if (flag) {
                    this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                } else {
                    this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                }

                this.fireChartChanged();
            }
        }

        public Object getTextAntiAlias() {
            return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        }

        public void setTextAntiAlias(boolean flag) {
            if (flag) {
                this.setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            } else {
                this.setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }

        }

        public void setTextAntiAlias(Object val) {
            this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
            this.notifyListeners(new ChartChangeEvent(this));
        }

        public Paint getBackgroundPaint() {
            return this.backgroundPaint;
        }

        public void setBackgroundPaint(Paint paint) {
            if (this.backgroundPaint != null) {
                if (!this.backgroundPaint.equals(paint)) {
                    this.backgroundPaint = paint;
                    this.fireChartChanged();
                }
            } else if (paint != null) {
                this.backgroundPaint = paint;
                this.fireChartChanged();
            }

        }

        public Image getBackgroundImage() {
            return this.backgroundImage;
        }

        public void setBackgroundImage(Image image) {
            if (this.backgroundImage != null) {
                if (!this.backgroundImage.equals(image)) {
                    this.backgroundImage = image;
                    this.fireChartChanged();
                }
            } else if (image != null) {
                this.backgroundImage = image;
                this.fireChartChanged();
            }

        }

        public int getBackgroundImageAlignment() {
            return this.backgroundImageAlignment;
        }

        public void setBackgroundImageAlignment(int alignment) {
            if (this.backgroundImageAlignment != alignment) {
                this.backgroundImageAlignment = alignment;
                this.fireChartChanged();
            }

        }

        public float getBackgroundImageAlpha() {
            return this.backgroundImageAlpha;
        }

        public void setBackgroundImageAlpha(float alpha) {
            if (this.backgroundImageAlpha != alpha) {
                this.backgroundImageAlpha = alpha;
                this.fireChartChanged();
            }

        }

        public boolean isNotify() {
            return this.notify;
        }

        public void setNotify(boolean notify) {
            this.notify = notify;
            if (notify) {
                this.notifyListeners(new ChartChangeEvent(this));
            }

        }

        public void draw(Graphics2D g2, Rectangle2D area) {
            this.draw(g2, area, (Point2D)null, (ChartRenderingInfo)null);
        }

        public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
            this.draw(g2, area, (Point2D)null, info);
        }

        public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
            this.notifyListeners(new ChartProgressEvent(this, this, 1, 0));
            if (info != null) {
                info.clear();
                info.setChartArea(chartArea);
            }

            Shape savedClip = g2.getClip();
            g2.clip(chartArea);
            g2.addRenderingHints(this.renderingHints);
            if (this.backgroundPaint != null) {
                g2.setPaint(this.backgroundPaint);
                g2.fill(chartArea);
            }

            if (this.backgroundImage != null) {
                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
                Rectangle2D dest = new Rectangle2D.Double((double)0.0F, (double)0.0F, (double)this.backgroundImage.getWidth((ImageObserver)null), (double)this.backgroundImage.getHeight((ImageObserver)null));
                Align.align(dest, chartArea, this.backgroundImageAlignment);
                g2.drawImage(this.backgroundImage, (int)dest.getX(), (int)dest.getY(), (int)dest.getWidth(), (int)dest.getHeight(), (ImageObserver)null);
                g2.setComposite(originalComposite);
            }

            if (this.isBorderVisible()) {
                Paint paint = this.getBorderPaint();
                Stroke stroke = this.getBorderStroke();
                if (paint != null && stroke != null) {
                    Rectangle2D borderArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(), chartArea.getWidth() - (double)1.0F, chartArea.getHeight() - (double)1.0F);
                    g2.setPaint(paint);
                    g2.setStroke(stroke);
                    g2.draw(borderArea);
                }
            }

            Rectangle2D nonTitleArea = new Rectangle2D.Double();
            nonTitleArea.setRect(chartArea);
            this.padding.trim(nonTitleArea);
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getEntityCollection();
            }

            if (this.title != null) {
                EntityCollection e = this.drawTitle(this.title, g2, nonTitleArea, entities != null);
                if (e != null) {
                    entities.addAll(e);
                }
            }

            for(Title currentTitle : this.subtitles) {
                if (currentTitle.isVisible()) {
                    EntityCollection e = this.drawTitle(currentTitle, g2, nonTitleArea, entities != null);
                    if (e != null) {
                        entities.addAll(e);
                    }
                }
            }

            PlotRenderingInfo plotInfo = null;
            if (info != null) {
                plotInfo = info.getPlotInfo();
            }

            this.plot.draw(g2, nonTitleArea, anchor, (PlotState)null, plotInfo);
            g2.setClip(savedClip);
            this.notifyListeners(new ChartProgressEvent(this, this, 2, 100));
        }

        private Rectangle2D createAlignedRectangle2D(Size2D dimensions, Rectangle2D frame, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
            double x = Double.NaN;
            double y = Double.NaN;
            if (hAlign == HorizontalAlignment.LEFT) {
                x = frame.getX();
            } else if (hAlign == HorizontalAlignment.CENTER) {
                x = frame.getCenterX() - dimensions.width / (double)2.0F;
            } else if (hAlign == HorizontalAlignment.RIGHT) {
                x = frame.getMaxX() - dimensions.width;
            }

            if (vAlign == VerticalAlignment.TOP) {
                y = frame.getY();
            } else if (vAlign == VerticalAlignment.CENTER) {
                y = frame.getCenterY() - dimensions.height / (double)2.0F;
            } else if (vAlign == VerticalAlignment.BOTTOM) {
                y = frame.getMaxY() - dimensions.height;
            }

            return new Rectangle2D.Double(x, y, dimensions.width, dimensions.height);
        }

        protected EntityCollection drawTitle(Title t, Graphics2D g2, Rectangle2D area, boolean entities) {
            if (t == null) {
                throw new IllegalArgumentException("Null 't' argument.");
            } else if (area == null) {
                throw new IllegalArgumentException("Null 'area' argument.");
            } else {
                new Rectangle2D.Double();
                RectangleEdge position = t.getPosition();
                double ww = area.getWidth();
                if (ww <= (double)0.0F) {
                    return null;
                } else {
                    double hh = area.getHeight();
                    if (hh <= (double)0.0F) {
                        return null;
                    } else {
                        RectangleConstraint constraint = new RectangleConstraint(ww, new Range((double)0.0F, ww), LengthConstraintType.RANGE, hh, new Range((double)0.0F, hh), LengthConstraintType.RANGE);
                        Object retValue = null;
                        BlockParams p = new BlockParams();
                        p.setGenerateEntities(entities);
                        if (position == RectangleEdge.TOP) {
                            Size2D size = t.arrange(g2, constraint);
                            Rectangle2D titleArea = this.createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP);
                            retValue = t.draw(g2, titleArea, p);
                            area.setRect(area.getX(), Math.min(area.getY() + size.height, area.getMaxY()), area.getWidth(), Math.max(area.getHeight() - size.height, (double)0.0F));
                        } else if (position == RectangleEdge.BOTTOM) {
                            Size2D size = t.arrange(g2, constraint);
                            Rectangle2D var16 = this.createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
                            retValue = t.draw(g2, var16, p);
                            area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
                        } else if (position == RectangleEdge.RIGHT) {
                            Size2D size = t.arrange(g2, constraint);
                            Rectangle2D var17 = this.createAlignedRectangle2D(size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment());
                            retValue = t.draw(g2, var17, p);
                            area.setRect(area.getX(), area.getY(), area.getWidth() - size.width, area.getHeight());
                        } else {
                            if (position != RectangleEdge.LEFT) {
                                throw new RuntimeException("Unrecognised title position.");
                            }

                            Size2D size = t.arrange(g2, constraint);
                            Rectangle2D var18 = this.createAlignedRectangle2D(size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment());
                            retValue = t.draw(g2, var18, p);
                            area.setRect(area.getX() + size.width, area.getY(), area.getWidth() - size.width, area.getHeight());
                        }

                        EntityCollection result = null;
                        if (retValue instanceof EntityBlockResult) {
                            EntityBlockResult ebr = (EntityBlockResult)retValue;
                            result = ebr.getEntityCollection();
                        }

                        return result;
                    }
                }
            }
        }

        public BufferedImage createBufferedImage(int width, int height) {
            return this.createBufferedImage(width, height, (ChartRenderingInfo)null);
        }

        public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
            return this.createBufferedImage(width, height, 2, info);
        }

        public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
            BufferedImage image = new BufferedImage(width, height, imageType);
            Graphics2D g2 = image.createGraphics();
            this.draw(g2, new Rectangle2D.Double((double)0.0F, (double)0.0F, (double)width, (double)height), (Point2D)null, info);
            g2.dispose();
            return image;
        }

        public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, 2);
            Graphics2D g2 = image.createGraphics();
            double scaleX = (double)imageWidth / drawWidth;
            double scaleY = (double)imageHeight / drawHeight;
            AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
            g2.transform(st);
            this.draw(g2, new Rectangle2D.Double((double)0.0F, (double)0.0F, drawWidth, drawHeight), (Point2D)null, info);
            g2.dispose();
            return image;
        }

        public void handleClick(int x, int y, ChartRenderingInfo info) {
            this.plot.handleClick(x, y, info.getPlotInfo());
        }

        public void addChangeListener(ChartChangeListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Null 'listener' argument.");
            } else {
                this.changeListeners.add(class$org$jfree$chart$event$ChartChangeListener == null ? (class$org$jfree$chart$event$ChartChangeListener = class$("org.jfree.chart.event.ChartChangeListener")) : class$org$jfree$chart$event$ChartChangeListener, listener);
            }
        }

        public void removeChangeListener(ChartChangeListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Null 'listener' argument.");
            } else {
                this.changeListeners.remove(class$org$jfree$chart$event$ChartChangeListener == null ? (class$org$jfree$chart$event$ChartChangeListener = class$("org.jfree.chart.event.ChartChangeListener")) : class$org$jfree$chart$event$ChartChangeListener, listener);
            }
        }

        public void fireChartChanged() {
            ChartChangeEvent event = new ChartChangeEvent(this);
            this.notifyListeners(event);
        }

        protected void notifyListeners(ChartChangeEvent event) {
            if (this.notify) {
                Object[] listeners = this.changeListeners.getListenerList();

                for(int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == (class$org$jfree$chart$event$ChartChangeListener == null ? (class$org$jfree$chart$event$ChartChangeListener = class$("org.jfree.chart.event.ChartChangeListener")) : class$org$jfree$chart$event$ChartChangeListener)) {
                        ((ChartChangeListener)listeners[i + 1]).chartChanged(event);
                    }
                }
            }

        }

        public void addProgressListener(ChartProgressListener listener) {
            this.progressListeners.add(class$org$jfree$chart$event$ChartProgressListener == null ? (class$org$jfree$chart$event$ChartProgressListener = class$("org.jfree.chart.event.ChartProgressListener")) : class$org$jfree$chart$event$ChartProgressListener, listener);
        }

        public void removeProgressListener(ChartProgressListener listener) {
            this.progressListeners.remove(class$org$jfree$chart$event$ChartProgressListener == null ? (class$org$jfree$chart$event$ChartProgressListener = class$("org.jfree.chart.event.ChartProgressListener")) : class$org$jfree$chart$event$ChartProgressListener, listener);
        }

        protected void notifyListeners(ChartProgressEvent event) {
            Object[] listeners = this.progressListeners.getListenerList();

            for(int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == (class$org$jfree$chart$event$ChartProgressListener == null ? (class$org$jfree$chart$event$ChartProgressListener = class$("org.jfree.chart.event.ChartProgressListener")) : class$org$jfree$chart$event$ChartProgressListener)) {
                    ((ChartProgressListener)listeners[i + 1]).chartProgress(event);
                }
            }

        }

        public void titleChanged(TitleChangeEvent event) {
            event.setChart(this);
            this.notifyListeners((ChartChangeEvent)event);
        }

        public void plotChanged(PlotChangeEvent event) {
            event.setChart(this);
            this.notifyListeners((ChartChangeEvent)event);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof JFreeChart)) {
                return false;
            } else {
                JFreeChart that = (JFreeChart)obj;
                if (!this.renderingHints.equals(that.renderingHints)) {
                    return false;
                } else if (this.borderVisible != that.borderVisible) {
                    return false;
                } else if (!ObjectUtilities.equal(this.borderStroke, that.borderStroke)) {
                    return false;
                } else if (!PaintUtilities.equal(this.borderPaint, that.borderPaint)) {
                    return false;
                } else if (!this.padding.equals(that.padding)) {
                    return false;
                } else if (!ObjectUtilities.equal(this.title, that.title)) {
                    return false;
                } else if (!ObjectUtilities.equal(this.subtitles, that.subtitles)) {
                    return false;
                } else if (!ObjectUtilities.equal(this.plot, that.plot)) {
                    return false;
                } else if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
                    return false;
                } else if (!ObjectUtilities.equal(this.backgroundImage, that.backgroundImage)) {
                    return false;
                } else if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
                    return false;
                } else if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
                    return false;
                } else {
                    return this.notify == that.notify;
                }
            }
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            SerialUtilities.writeStroke(this.borderStroke, stream);
            SerialUtilities.writePaint(this.borderPaint, stream);
            SerialUtilities.writePaint(this.backgroundPaint, stream);
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.borderStroke = SerialUtilities.readStroke(stream);
            this.borderPaint = SerialUtilities.readPaint(stream);
            this.backgroundPaint = SerialUtilities.readPaint(stream);
            this.progressListeners = new EventListenerList();
            this.changeListeners = new EventListenerList();
            this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (this.title != null) {
                this.title.addChangeListener(this);
            }

            for(int i = 0; i < this.getSubtitleCount(); ++i) {
                this.getSubtitle(i).addChangeListener(this);
            }

            this.plot.addChangeListener(this);
        }

        public static void main(String[] args) {
            System.out.println(INFO.toString());
        }

        public Object clone() throws CloneNotSupportedException {
            JFreeChart chart = (JFreeChart)super.clone();
            chart.renderingHints = (RenderingHints)this.renderingHints.clone();
            if (this.title != null) {
                chart.title = (TextTitle)this.title.clone();
                chart.title.addChangeListener(chart);
            }

            chart.subtitles = new ArrayList();

            for(int i = 0; i < this.getSubtitleCount(); ++i) {
                Title subtitle = (Title)this.getSubtitle(i).clone();
                chart.subtitles.add(subtitle);
                subtitle.addChangeListener(chart);
            }

            if (this.plot != null) {
                chart.plot = (Plot)this.plot.clone();
                chart.plot.addChangeListener(chart);
            }

            chart.progressListeners = new EventListenerList();
            chart.changeListeners = new EventListenerList();
            return chart;
        }

}

/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package generic.text;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.*;
import java.util.List;

/**
 * Graphics used to render copied text data.  This class is not a true graphics object, but is
 * instead used to grab text being painted so that clients can later use that text.
 */
public class TextLayoutGraphics extends Graphics2D {

	private int transX;
	private int transY;
	private Shape clip;
	private StringBuilder buffer = new StringBuilder();
	private Font lastFont = new Font("SansSerif", Font.PLAIN, 12);
	private FontMetrics fontMetrics = createFontMetrics(lastFont);

	private List<TextInfo> textInfos = new ArrayList<>();

	private Comparator<TextInfo> pointComparator = (o1, o2) -> {
		TextInfo t1 = o1;
		TextInfo t2 = o2;

		int diff = t1.point.y - t2.point.y;
		if (diff != 0) {
			return diff;
		}

		diff = t1.point.x - t2.point.x;

		return diff;
	};

	private Comparator<TextInfo> rowComparator = (o1, o2) -> {
		TextInfo t1 = o1;
		TextInfo t2 = o2;

		int diff = t1.row - t2.row;
		if (diff != 0) {
			return diff;
		}

		diff = t1.point.x - t2.point.x;

		return diff;
	};

	private static FontMetrics createFontMetrics(Font font) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = image.getGraphics();
		FontMetrics fm = g.getFontMetrics(font);
		g.dispose();
		return fm;
	}

	@Override
	public void drawString(String str, float x, float y) {
		drawString(str, (int) x, (int) y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator.toString(), x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		drawString(iterator.toString(), (int) x, (int) y);
	}

	@Override
	public void drawString(String str, int x, int y) {
		TextInfo newTextInfo = new TextInfo();
		newTextInfo.point = new Point(x + transX, y + transY);
		newTextInfo.text = str;
		newTextInfo.font = lastFont;
		newTextInfo.fontMetrics = fontMetrics;
		textInfos.add(newTextInfo);
	}

	@Override
	public void setFont(Font font) {
		lastFont = font;
		fontMetrics = createFontMetrics(font);
	}

	/**
	 * Format text into a string for rendering.
	 */
	public void flush() {

		if (textInfos.isEmpty()) {
			return;
		}

		sortDataAndAssignRows();

		// render the text into a string
		int lastRow = 0;
		int currentX = 0; //The x coordinate of the end of the last string
		for (TextInfo info : textInfos) {
			// insert newlines as appropriate
			for (int i = lastRow; i < info.row; i++) {
				buffer.append('\n');
			}

			// if we started a new row, reset the x position
			if (lastRow != info.row) {
				currentX = 0;
			}
			lastRow = info.row;

			// insert spaces to account for distance past last field in row
			FontMetrics metrics = info.fontMetrics;
			int spaceWidth = metrics.charWidth(' ');

			if (spaceWidth == 0) {
				// some environments report 0 for some fonts
				spaceWidth = 4;
			}

			float spaceBetween = info.point.x - currentX;
			int fillSpaces = Math.round(spaceBetween / spaceWidth);

			// account for the case where there's a very small amount of space between fields
			if (fillSpaces == 0 && info.point.x > currentX) {
				fillSpaces = 1;
			}

			for (int i = 0; i < fillSpaces; i++) {
				buffer.append(' ');
			}

			int stringWidth = metrics.stringWidth(info.text);
			currentX = info.point.x + stringWidth;

			// append the text
			buffer.append(info.text);
		}

		buffer.append('\n');
		textInfos.clear();
	}

	private void sortDataAndAssignRows() {

		// sort the text by y position, then by x position
		textInfos.sort(pointComparator);

		// Group the text into rows based on font height and y position
		//TODO - Ideally, it would be nice if there was a good way to group text that
		//       varied in height in a nice way
		int lastPos = textInfos.get(0).point.y;
		int row = 0;
		for (TextInfo info : textInfos) {
			if (info.point.y != lastPos) {
				row++;
				lastPos = info.point.y;
			}

			info.row = row;
		}

		// sort the text by row, then by x position
		textInfos.sort(rowComparator);

	}

	public String getBuffer() {
		return buffer.toString();
	}

	@Override
	public void translate(int x, int y) {
		transX += x;
		transY += y;
	}

	@Override
	public Font getFont() {
		return lastFont;
	}

	@Override
	public Graphics create() {
		return this;
	}

	@Override
	public Rectangle getClipBounds() {
		return clip.getBounds();
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void setClip(Shape clip) {
		this.clip = clip;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return fontMetrics;
	}

	@Override
	public void setClip(int x, int y, int width, int localHeight) {
		this.clip = new Rectangle(x, y, width, localHeight);
	}

//==================================================================================================
// Stubs
//==================================================================================================

	@Override
	public void dispose() {
		// stub
	}

	@Override
	public void setPaintMode() {
		// stub
	}

	@Override
	public void clearRect(int x, int y, int width, int localHeight) {
		// stub
	}

	@Override
	public void clipRect(int x, int y, int width, int localHeight) {
		// stub
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// stub
	}

	@Override
	public void drawOval(int x, int y, int width, int localHeight) {
		// stub
	}

	@Override
	public void fillOval(int x, int y, int width, int localHeight) {
		// stub
	}

	@Override
	public void fillRect(int x, int y, int width, int localHeight) {
		// stub
	}

	@Override
	public void copyArea(int x, int y, int width, int localHeight, int dx, int dy) {
		// stub
	}

	@Override
	public void drawArc(int x, int y, int width, int localHeight, int startAngle, int arcAngle) {
		// stub
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int localHeight, int arcWidth,
			int arcHeight) {
		// stub
	}

	@Override
	public void fillArc(int x, int y, int width, int localHeight, int startAngle, int arcAngle) {
		// stub
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int localHeight, int arcWidth,
			int arcHeight) {
		// stub
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// stub
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		// stub
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// stub
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public void setColor(Color c) {
		// stub
	}

	@Override
	public void setXORMode(Color c1) {
		// stub
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, ImageObserver observer) {
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int localHeight,
			ImageObserver observer) {
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int localHeight, Color bgcolor,
			ImageObserver observer) {
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return false;
	}

	@Override
	public void draw(Shape s) {
		// stub
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// stub
		return false;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// stub
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// stub
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// stub
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// stub
	}

	@Override
	public void fill(Shape s) {
		// stub
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// stub
		return false;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// stub
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		// stub
	}

	@Override
	public void setPaint(Paint paint) {
		// stub
	}

	@Override
	public void setStroke(Stroke s) {
		// stub
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// stub
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		// stub
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// stub
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// stub
	}

	@Override
	public RenderingHints getRenderingHints() {
		// stub
		return null;
	}

	@Override
	public void translate(double tx, double ty) {
		// stub
	}

	@Override
	public void rotate(double theta) {
		// stub
	}

	@Override
	public void rotate(double theta, double x, double y) {
		// stub
	}

	@Override
	public void scale(double sx, double sy) {
		// stub
	}

	@Override
	public void shear(double shx, double shy) {
		// stub
	}

	@Override
	public void transform(AffineTransform Tx) {
		// stub
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// stub
	}

	@Override
	public AffineTransform getTransform() {
		// Some UIs (e.g. FlatLookAndFeel) expect a transform to be applied in some situations
		// depending on high resolution monitor settings
		return new AffineTransform();
	}

	@Override
	public Paint getPaint() {
		// stub
		return null;
	}

	@Override
	public Composite getComposite() {
		// stub
		return null;
	}

	@Override
	public void setBackground(Color color) {
		// stub
	}

	@Override
	public Color getBackground() {
		// stub
		return null;
	}

	@Override
	public Stroke getStroke() {
		// stub
		return null;
	}

	@Override
	public void clip(Shape s) {
		// stub
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// stub
		return null;
	}

}

class TextInfo {
	String text;
	Font font;
	FontMetrics fontMetrics;
	Point point;
	int row;

	@Override
	public String toString() {
		//@formatter:off
		return "{\n" +
			"\ttext: " + text + "\n" +
			"\tpoint: " + point + "\n" +
		"}";
		//@formatter:on
	}
}

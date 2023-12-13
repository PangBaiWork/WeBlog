package com.pangbai.weblog.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pangbai.weblog.editor.handle.BlockQuoteEditHandler;
import com.pangbai.weblog.editor.handle.CodeEditHandler;
import com.pangbai.weblog.editor.handle.HeadingEditHandler;
import com.pangbai.weblog.editor.handle.LinkEditHandler;
import com.pangbai.weblog.editor.handle.StrikethroughEditHandler;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executors;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.EntityInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.inlineparser.NewLineInlineProcessor;
import io.noties.markwon.linkify.LinkifyPlugin;

public class rander {
  public static void    randerEditor(EditText editText, Context context){



      editText.setMovementMethod(LinkMovementMethod.getInstance());
      // when automatic line break is inserted and text is inside margin span (blockquote, list, etc)
      //  be prepared to encounter selection bugs (selection would be drawn at the place as is no margin
      //  span is present)
      final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
              // no inline images will be parsed
              .excludeInlineProcessor(BangInlineProcessor.class)
              // no html tags will be parsed
              .excludeInlineProcessor(HtmlInlineProcessor.class)
              // no entities will be parsed (aka `&amp;` etc)
              .excludeInlineProcessor(EntityInlineProcessor.class)
              .excludeInlineProcessor(NewLineInlineProcessor.class)
              .excludeInlineProcessor(EntityInlineProcessor.class)
              //.excludeDelimiterProcessor(AsteriskDelimiterProcessor.class)
             // .excludeDelimiterProcessor(UnderscoreDelimiterProcessor.class)
              .build();
// disable _all_ markdown inlines except for links (open and close bracket handling `[` & `]`)


      final Markwon markwon = Markwon.builder(context)
            // .usePlugin(SoftBreakAddsNewLinePlugin.create())
              .usePlugin(StrikethroughPlugin.create())
              .usePlugin(LinkifyPlugin.create())
              .usePlugin(MarkwonInlineParserPlugin.create())
             // .usePlugin(SyntaxHighlightPlugin.create(new Prism4j(\)))
              .usePlugin(new AbstractMarkwonPlugin() {
                  @Override
                  public void configureParser(@NonNull Parser.Builder builder) {
                    builder.inlineParserFactory(inlineParserFactory);

                      // disable all commonmark-java blocks, only inlines will be parsed
                        HashSet parser=  new HashSet<>(Arrays.asList(Heading.class, ListBlock.class,Heading.class,
                                ThematicBreak.class, IndentedCodeBlock.class, BlockQuote.class));

                    //  IndentedCodeBlock.class???
              //      builder.enabledBlockTypes(parser);


                  }
              })
              .build();

      final MarkwonEditor editor = MarkwonEditor.builder(markwon)
              .punctuationSpan(HidePunctuationSpan.class, HidePunctuationSpan::new)
              .useEditHandler(new EmphasisEditHandler())
              .useEditHandler(new StrongEmphasisEditHandler())
              .useEditHandler(new StrikethroughEditHandler())
              .useEditHandler(new CodeEditHandler())
              .useEditHandler(new BlockQuoteEditHandler())

              .useEditHandler(new LinkEditHandler(new LinkEditHandler.OnClick() {
                  @Override
                  public void onClick(@NonNull View widget, @NonNull String link) {
                      Log.e("clicked: %s", link);
                  }
              }))
              .useEditHandler(new HeadingEditHandler())
              .build();

      // for links to be clickable
      //   NB! markwon MovementMethodPlugin cannot be used here as editor do not execute `beforeSetText`)
      editText.setMovementMethod(LinkMovementMethod.getInstance());

      editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
              editor, Executors.newSingleThreadExecutor(), editText));
  }

    private static class HidePunctuationSpan extends ReplacementSpan {

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            // last space (which is swallowed until next non-space character appears)
            // block quote
            // code tick

//      Debug.i("text: '%s', %d-%d (%d)", text.subSequence(start, end), start, end, text.length());
            if (end == text.length()) {
                // TODO: find first non-space character (not just first one because commonmark allows
                //  arbitrary (0-3) white spaces before content starts

                //  TODO: if all white space - render?
                final char c = text.charAt(start);
                if ('#' == c
                        || '>' == c
                        || '-' == c // TODO: not thematic break
                        || '+' == c
                        // `*` is fine but only for a list
                        || isBulletList(text, c, start, end)
                        || Character.isDigit(c) // assuming ordered list (replacement should only happen for ordered lists)
                        || Character.isWhitespace(c)) {
                    return (int) (paint.measureText(text, start, end) + 0.5F);
                }
            }
            return 0;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            // will be called only when getSize is not 0 (and if it was once reported as 0...)
            if (end == text.length()) {

                // if first non-space is `*` then check for is bullet
                //  else `**` would be still rendered at the end of the emphasis
                if (text.charAt(start) == '*'
                        && !isBulletList(text, '*', start, end)) {
                    return;
                }

                // TODO: inline code last tick received here, handle it (do not highlight)
                //  why can't we have reported width in this method for supplied text?

                // let's use color to make it distinct from the rest of the text for demonstration purposes
                paint.setColor(Color.RED);

                canvas.drawText(text, start, end, x, y, paint);
            }
        }

        private static boolean isBulletList(@NonNull CharSequence text, char firstChar, int start, int end) {
            return '*' == firstChar
                    && ((end - start == 1) || (Character.isWhitespace(text.charAt(start + 1))));
        }
    }
}
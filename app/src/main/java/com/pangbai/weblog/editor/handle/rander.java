package com.pangbai.weblog.editor.handle;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.concurrent.Executors;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.EntityInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.linkify.LinkifyPlugin;

public class rander {
  public static void    randerEditor(EditText editText, Context context){


          // for links to be clickable
          editText.setMovementMethod(LinkMovementMethod.getInstance());

        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
                  // no inline images will be parsed
                  .excludeInlineProcessor(BangInlineProcessor.class)
                  // no html tags will be parsed
                  .excludeInlineProcessor(HtmlInlineProcessor.class)
                  // no entities will be parsed (aka `&amp;` etc)
                  .excludeInlineProcessor(EntityInlineProcessor.class)
                  .build();

          final Markwon markwon = Markwon.builder(context)
                  .usePlugin(StrikethroughPlugin.create())
                  .usePlugin(LinkifyPlugin.create())
                  .usePlugin(new AbstractMarkwonPlugin() {
                      @Override
                      public void configureParser(@NonNull Parser.Builder builder) {

                          // disable all commonmark-java blocks, only inlines will be parsed
//          builder.enabledBlockTypes(Collections.emptySet());

                          builder.inlineParserFactory(inlineParserFactory);
                      }
                  })
                  .usePlugin(SoftBreakAddsNewLinePlugin.create())
                  .build();

          final LinkEditHandler.OnClick onClick = (widget, link) -> markwon.configuration().linkResolver().resolve(widget, link);

          final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                  .useEditHandler(new EmphasisEditHandler())
                  .useEditHandler(new StrongEmphasisEditHandler())
                  .useEditHandler(new StrikethroughEditHandler())
                  .useEditHandler(new CodeEditHandler())
                  .useEditHandler(new io.noties.markwon.app.samples.editor.shared.BlockQuoteEditHandler())
                  .useEditHandler(new LinkEditHandler(onClick))
                  .build();

          editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                  editor, Executors.newSingleThreadExecutor(), editText));
      }



}

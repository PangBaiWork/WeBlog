# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class br.tiagohm.markdownview.MarkdownView {
   public *;
}



 -keepattributes *Annotation*
 -keepattributes *JavascriptInterface*



-dontwarn java.beans.BeanInfo
-dontwarn java.beans.FeatureDescriptor
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn java.awt.event.ActionListener
-dontwarn javax.swing.SwingUtilities
-dontwarn javax.swing.Timer
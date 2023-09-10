## What's new:

### Breaking changes for content, analytics and notable for others.

### Analytics

* Unique IDs are now generated for each mod separately and are no longer stored in the config.
  * You need a ModContainer to generate an instance.
* Removed Decider from Crashlytics.

### Base

* Introducing the new and improved `ExtendablePlugin`!
  * This is a successor to `ExtendedPlugin`, but with more features!
  * One of the main feature is plugin support (Plugin-Plugins)
  * Default plugins include: `MixinPredicatePlugin`, `ShouldApplyPlugin`, `PublicizePlugin`.
  * Only MixinPredicatePlugin is enabled by default.
  * Plugins can be added using the inner `DefaultPlugins` class.
* Added `@MixinPredicate`. Part of `MixinPredicatePlugin`
  * This annotation replaces `@MixinShouldApply`.
  * This also comes with a better version of `@Mod`, where you can specify a version predicate.
* Added `@Publicize`. Part of `PublicizePlugin`
  * Patches annotated fields and methods from `private` to `public`.
  * This is meant for static members that need to be accessed from the outside.
* Added `@ConstructDummy`. Part of `ConstructDummyPlugin`
  * If not present, attempts to construct a dummy override of a method.
  * This is only meant for `HEAD` and `TAIL` `@Inject`s.
  * "Use at your own risk".
* Added `AsmUtil`.
  * This includes `mapAnnotationNode()` and `mapObjectFromAnnotation()` from `ExtendedPlugin`.
* Added `of()` methods to `Tuple` and `MutableTuple`.
* Fixed `@MixinShouldApply` skipping every second mod.
* Updated MixinExtras.

### Content

* Split api/impl in content builder.
* Added `ItemGroup` as a parameter of `AnimatedItemGroup`.

### Enums

* A debug message is now logged after extending enums.

### Glitter

* The passed MatrixStack should be new in less cases. (<1.20)

### Minecraft

* ValueTracker gets a facelift!
  * Instead of using fields/classes as IDs, we use actual string IDs.
  * Now you can track anything with a supplier!
  * Reflection is still supported, but wrapped in a supplier.
  * You can now add timed trackers that disappear when the timer is up.
  * So it should actually be useful now.

### Recipe Book

* Methods in `RecipeBookHelper` have been renamed to make more sense and better represent what they do.
  * Old methods still exist, but are deprecated.
  * The singular group parameter has been replaced with varargs.
  * `addToSearchGroup()`, `registerGroups()` and `registerAndAddToSearch()` should have overload parity.
* Added `registerAndAddToSearch()` to `RecipeBookHelper`. Saves one line!
* Duplicate groups should now be handled better.

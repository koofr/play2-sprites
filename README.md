# play2-sprites

play2-sprites is an sbt plugin that generates sprites from images.

CSS classes will be named by file names (`my_icon.png` -> `.my-icon`) by default.

## Example

http://play2-sprites-demo.herokuapp.com/

**CSS file:**

http://play2-sprites-demo.herokuapp.com/assets/stylesheets/style.css

**Sprite image:**

http://play2-sprites-demo.herokuapp.com/assets/images/sprites.png

## Usage

Add following lines to your `project/plugins.sbt` file:

    resolvers += Resolver.url("Github repo", url("http://koofr.github.com/repo/releases/"))(Resolver.ivyStylePatterns)

    addSbtPlugin("net.koofr" % "play2-sprites" % "0.1.0")

Add following import to your `Build.scala` file:

    import net.koofr.play2sprites.GenerateSprites._

Extend default settings with `genSpritesSettings`:

    settings = Defaults.defaultSettings ++ genSpritesSettings

Now add following settings:

    spritesSrcImages <<= baseDirectory( (base: File) => base / "public/images/sprites" * "*.png" ),
    spritesDestImage <<= baseDirectory( (base: File) => base / "public/images/sprites.png" ),
    spritesCssSpritePath := "../images/sprites.png",
    spritesDestCss <<= baseDirectory( (base: File) => base / "public/stylesheets/sprites.css" )

    resourceGenerators in Compile <+= spritesGen,

If you use Less, `spriteGen` must be executed before Play's `LessCompiler`:

    resourceGenerators in Compile ~= { gens => gens.last +: gens.init }

Full example can be found in sample application.

### Less

If you use Less, you can generate `_sprites.less` file and include it in your main less file.

    spritesDestCss <<= baseDirectory( (base: File) => base / "app/assets/stylesheets/_sprites.less" ),

Your main less file (e.g. `main.less`):

    @import "_sprites.less";

You can also use sprite classes as mixins:

    .my-button {
      .my-icon();
    }

### Prefix

You can also prefix CSS classes:

    spritesCssClassPrefix := "sprite-"

Now sprite classes will be named as `.sprite-my-icon`.

## Sample

Sample Play application is available in `sample` directory.

## Local build

To build the plugin locally and publish it to your local filesystem:

    $ sbt publish-local

## Authors

Crafted by highly motivated engineers at http://koofr.net and, hopefully, making your day just a little bit better.

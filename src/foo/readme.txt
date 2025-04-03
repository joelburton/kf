# get all interfaces & Word so add-on can built w/o internal knowledge
kotlinc -d /tmp/somewhere ~/kf/src/main/kotlin/kf/interfaces/* ~/kf/src/main/kotlin/kf/dict/Word.kt

# don't need a jar, but it's nice and tidy
jar cf kf-public.jar kf

## only need to do this when interfaces change ^
## do this when new module changes \/

# compile the add-on
kotlinc -cp kf-public.jar NewWordMod.kt
jar cf kf-addon.jar *class

# run and you can bload NewWordMod & then have foofoo and barbar words
java -cp ~/src/kf/build/libs/kf-all.jar:kf-addon.jar kf.cli.MainKt

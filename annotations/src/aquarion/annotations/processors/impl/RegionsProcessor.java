package aquarion.annotations.processors.impl;

import aquarion.annotations.Annotations.Load;
import aquarion.annotations.processors.BaseProcessor;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import mindustry.ctype.MappableContent;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

/**
 * Whenever a {@link arc.graphics.g2d.TextureRegion TextureRegion} is annotated with {@link Load @Load},
 * it'll generate a finder method inside the ContentRegionRegistry, which must be called for every
 * content in {@link mindustry.game.EventType.ContentInitEvent ContentInitEvent}
 */
public class RegionsProcessor extends BaseProcessor{
	public ObjectMap<Element, Seq<Element>> annotated = new ObjectMap<>();

	{
		rounds = 1;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes(){
		Set<String> types = new HashSet<>();
		String prefix = processingEnv.getOptions().get("modName") + ".annotations.Annotations.";
		types.add(prefix + "Load");
		types.add(prefix + "EnsureLoad");
		return types;
	}

	@Override
	public void process(RoundEnvironment roundEnv) throws Exception{
		if(round == 1){
			TypeSpec.Builder regionsClass = TypeSpec.classBuilder(classPrefix + "ContentRegionRegistry")
					.addModifiers(Modifier.PUBLIC)
					.addJavadoc("Class generated for loading regions annotated with {@link aquarion.annotations.Annotations.Load load}");

			MethodSpec.Builder loadMethod = MethodSpec.methodBuilder("load")
					.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
					.addParameter(tName(MappableContent.class), "content");

			for(Element element : roundEnv.getElementsAnnotatedWith(Load.class)){
				TypeMirror type = element.asType();
				while(type.getKind() == TypeKind.ARRAY){
					type = ((ArrayType)type).getComponentType();
				}
				if(!types.isSameType(type, toType(TextureRegion.class).asType())){
					throw new IllegalAccessException("Only TextureRegions should be annotated with @Load");
				}

				annotated.get(element.getEnclosingElement(), Seq::new).add(element);
			}

			for(Element base : annotated.keys().toSeq()){

				loadMethod.beginControlFlow("if(content instanceof $T)", cName(base));

				for(Element field : annotated.get(base)){
					Load annotation = field.getAnnotation(Load.class);

					if (annotation.lengths().length > 0) {
						StringBuilder depth = new StringBuilder();
						for(int i = 0; i < annotation.lengths().length; i++){
							depth.append("[").append(annotation.lengths()[i]).append("]");
						}

						loadMethod.addStatement(
								"(($T)content).$L = new $T$L",
								cName(base),
								field.getSimpleName(),
								cName(TextureRegion.class),
								depth.toString()
						);
					}

					StringBuilder depth = new StringBuilder();
					for(int i = 0; i < annotation.lengths().length; i++){
						loadMethod.beginControlFlow(
								"for(int INDEX$L = 0; INDEX$L < $L; INDEX$L++)",
								i, i, annotation.lengths()[i], i
						);

						depth.append("[INDEX").append(i).append("]");
					}

					loadMethod.addStatement(
							"(($T)content).$L$L = $T.atlas.find($L, $L)",
							cName(base),
							field.getSimpleName(),
							depth.toString(),
							cName(Core.class),
							parse(annotation.value()),
							parse(annotation.fallBack())
					);

					for(int i = 0; i < annotation.lengths().length; i++){
						loadMethod.endControlFlow();
					}
				}

				loadMethod.endControlFlow();
			}

			regionsClass.addMethod(loadMethod.build());

			write(regionsClass.build());
		}
	}

	public String parse(String other){
		other = '"' + other + '"';
		return other
				.replace("@modname", modName)
				.replace("@size", "\" + ((mindustry.world.Block)content).size + \"")
				.replace("@", "\" + content.name + \"")
				.replace("#", "\" + INDEX")
				.replace("$", " + \"");
	}
}
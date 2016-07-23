package org.taoningyu.tools;

import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class AddLogAdapter extends ClassVisitor {
	public  String TAG = "TNY";
	public  String TEXT_PREFIX = "TNY TEST...";
	private String owner;
	private boolean isInterface;
	private String mezdRegx = "*";
	private String clazzRegx = "*";
	private String superNameRegx = "*";
	private String[] intfacRegx = null;
	public boolean valid = false;

	public AddLogAdapter(ClassVisitor cv, String TAG,String TEXT_PREFIX,String clazzRegx, String superNameRegx, String[] intfacRegx,
			String mezdRegx) {
		super(Opcodes.ASM5, cv);
		this.mezdRegx = mezdRegx;
		this.clazzRegx = clazzRegx;
		this.superNameRegx = superNameRegx;
		this.intfacRegx = intfacRegx;
		this.TAG = TAG;
		this.TEXT_PREFIX =TEXT_PREFIX;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		valid = validate(this.clazzRegx, this.superNameRegx, this.intfacRegx, name, superName, interfaces);
		cv.visit(version, access, name, signature, superName, interfaces);
		owner = name;
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
	}

	private boolean validate(String clazzRegx, String superNameRegx, String[] intfacRegx, String name, String superName,
			String[] interfaces) {
		if (name.matches(clazzRegx)) {
			if (superName.matches(superNameRegx)) {
				int matchedSucNum = 0;
				for (String tmpInt : interfaces) {
					boolean tmpResult = false;
					for (String tmpIntRexg : intfacRegx) {
						tmpResult = tmpInt.matches(tmpIntRexg);
						if (tmpResult) {
							matchedSucNum++;
							break;
						}
					}
					if (!tmpResult) {
						return false;
					}
				}
				if (matchedSucNum >= intfacRegx.length) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if (!isInterface && mv != null && !name.equals("<init>")) {
			if ((this.owner + "-" + desc + "-" + name).matches(mezdRegx) && valid) {
				mv = new AddTimerMethodAdapter(mv, access, this.owner, desc, name);
			}
		}
		return mv;
	}

	@Override
	public void visitEnd() {
		if (!isInterface) {

		}
		cv.visitEnd();
	}

	class AddTimerMethodAdapter extends MethodVisitor {
		String owner = "";
		String desc = "";
		String name = "";
		int access = -1;
		int startIndex = -1;

		public AddTimerMethodAdapter(MethodVisitor mv, int access, String owner, String desc, String name) {
			super(Opcodes.ASM5, mv);
			this.owner = owner;
			this.desc = desc;
			this.name = name;
			this.access = access;
			if ((access & Opcodes.ACC_STATIC) != 0) {
				// static method
				startIndex = 0;
			} else {
				startIndex = 1;
			}
		}

		public void addLocalVariablesLog() {
			int startIndexBk = this.startIndex;
			ArrayList<String> variables = new ArrayList<String>();
			variables.add("Method name: " + this.owner + "-" + this.name + "-" + this.desc);
			boolean waitSemic = false;
			boolean stop = false;
			for (int i = 0; i < this.desc.length(); i++) {

				switch (this.desc.charAt(i)) {

				case 'I':
					if (waitSemic == false && !stop)
						variables.add("I");

					break;
				case 'S':
					if (waitSemic == false && !stop)
						variables.add("I");

					break;
				case 'Z':
					if (waitSemic == false && !stop)
						variables.add("I");

					break;
				case 'C':
					if (waitSemic == false && !stop)
						variables.add("I");

					break;
				case 'J':
					if (waitSemic == false && !stop)
						variables.add("J");

					break;
				case 'F':
					if (waitSemic == false && !stop)
						variables.add("F");

					break;
				case 'B':
					if (waitSemic == false && !stop)
						variables.add("I");

					break;
				case 'D':
					if (waitSemic == false && !stop)
						variables.add("D");

					break;
				case 'L':
					if (waitSemic == false && !stop) {

						variables.add("L");
						waitSemic = true;
					}

					break;
				case '[':
					if (waitSemic == false && !stop) {
						variables.add("L");
						waitSemic = true;
					}
					break;
				case ';':
					if (!stop)
						waitSemic = false;

					break;
				case ')':
					stop = true;

					break;
				default:
					break;

				}
			}

			for (String tmpVar : variables) {

				switch (tmpVar) {

				case "I":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ILOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "S":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ILOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "Z":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ILOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "C":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ILOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "J":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.LLOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;
					this.startIndex++;

					break;
				case "F":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.FLOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "B":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ILOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				case "D":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.DLOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;
					this.startIndex++;

					break;
				case "L":
					mv.visitLdcInsn(TAG);
					mv.visitVarInsn(Opcodes.ALOAD, this.startIndex);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;",
							false);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					this.startIndex++;

					break;
				default:
					mv.visitLdcInsn(TAG);
					mv.visitLdcInsn("Method name: " + this.owner + "-" + this.name + "-" + this.desc);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
							"(Ljava/lang/String;Ljava/lang/String;)I", false);
					mv.visitInsn(Opcodes.POP);
					break;
				}

			}
			this.startIndex = startIndexBk;
		}

		@Override
		public void visitCode() {
			mv.visitCode();
			addLocalVariablesLog();
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		//	System.out.println(desc + "\t" + visible);

			if (api < Opcodes.ASM5) {
				throw new RuntimeException();
			}
			if (mv != null) {
				return mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
			}
			return null;
		}

		@Override

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			//System.out.println(desc + "\t" + visible);

			if (mv != null) {
				return mv.visitAnnotation(desc, visible);
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			//System.out.println(desc + "\t" + visible);

			if (api < Opcodes.ASM5) {
				throw new RuntimeException();
			}
			if (mv != null) {
				return mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			//System.out.println(parameter + "\t" + desc + "\t" + visible);
			if (mv != null) {
				return mv.visitParameterAnnotation(parameter, desc, visible);
			}
			return null;
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			mv.visitMaxs(maxStack + 4, maxLocals);
		}
	}
}
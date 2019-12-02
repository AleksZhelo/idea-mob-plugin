package com.alekseyzhelo.evilislands.mobplugin.mob.psi;

import com.alekseyzhelo.eimob.MobFile;
import com.alekseyzhelo.eimob.MobVisitor;
import com.alekseyzhelo.eimob.blocks.ObjectsBlock;
import com.alekseyzhelo.eimob.objects.*;
import com.alekseyzhelo.evilislands.mobplugin.mob.psi.objects.*;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class PsiBuildingMobVisitor extends MobVisitor {

    private static final Logger LOG = Logger.getInstance(PsiBuildingMobVisitor.class);

    private final PsiMobFile myFile;
    private final PsiMobObjectsBlock objectsBlock;
    private final Map<Integer, PsiMobMapEntity> elementMap;

    static PsiMobObjectsBlock createPsiMobObjectsBlock(PsiMobFile file) {
        PsiBuildingMobVisitor visitor = new PsiBuildingMobVisitor(file);
        file.acceptMobVisitor(visitor);
        visitor.objectsBlock.setElements(visitor.elementMap);
        return visitor.objectsBlock;
    }

    private PsiBuildingMobVisitor(PsiMobFile file) {
        myFile = file;
        final MobFile mobFile = file.getMobFile();
        if (mobFile != null && mobFile.getObjectsBlock() != null) {
            elementMap = new HashMap<>(mobFile.getObjectsBlock().objectCount());
        } else {
            elementMap = new HashMap<>();
        }
        this.objectsBlock = new PsiMobObjectsBlock(file);
    }

    private void putChecked(int id, PsiMobMapEntity element) {
        PsiMobElement old = elementMap.put(id, element);
        if (old != null) {
            LOG.error(String.format("ID %d is not unique in %s!", id, myFile.getVirtualFile().getPath()));
        }
    }

    @Override
    public void visitObjectsBlock(@NotNull ObjectsBlock value) {
        value.acceptChildren(this);
    }

    @Override
    public void visitMobFlame(@NotNull MobFlame value) {
        putChecked(value.getId(), new PsiMobFlame(objectsBlock, value));
    }

    @Override
    public void visitMobLever(@NotNull MobLever value) {
        putChecked(value.getId(), new PsiMobLever(objectsBlock, value));
    }

    @Override
    public void visitMobLight(@NotNull MobLight value) {
        putChecked(value.getId(), new PsiMobLight(objectsBlock, value));
    }

    @Override
    public void visitMobObject(@NotNull MobObject value) {
        putChecked(value.getId(), new PsiMobObject(objectsBlock, value));
    }

    @Override
    public void visitMobParticle(@NotNull MobParticle value) {
        putChecked(value.getId(), new PsiMobParticle(objectsBlock, value));
    }

    @Override
    public void visitMobSound(@NotNull MobSound value) {
        putChecked(value.getId(), new PsiMobSound(objectsBlock, value));
    }

    @Override
    public void visitMobTrap(@NotNull MobTrap value) {
        putChecked(value.getId(), new PsiMobTrap(objectsBlock, value));
    }

    @Override
    public void visitMobUnit(@NotNull MobUnit value) {
        putChecked(value.getId(), new PsiMobUnit(objectsBlock, value));
    }
}

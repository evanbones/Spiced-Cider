package com.evandev.spicedcider.client.models.sporal;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class SporalAnimations {
    public static final AnimationDefinition BESTOW = AnimationDefinition.Builder.withLength(1.0F)
            .addAnimation("head", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.08333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -12.5F), Interpolations.CATMULLROM),
                    new Keyframe(0.58333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)))
            .addAnimation("upper", new AnimationChannel(Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.20833F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                    new Keyframe(0.70833F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)))
            .addAnimation("upper", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.20833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -15.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.70833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 15.0F), Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)))
            .build();

    public static final AnimationDefinition LEAP = AnimationDefinition.Builder.withLength(0.29167F)
            .addAnimation("leg1", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(50.0F, 0.0F, 15.0F), Interpolations.CATMULLROM)))
            .addAnimation("leg2", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                    new Keyframe(0.29167F, KeyframeAnimations.degreeVec(30.0F, 10.0F, 0.0F), Interpolations.CATMULLROM)))
            .addAnimation("upper", new AnimationChannel(Targets.ROTATION,
                    new Keyframe(0.04167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                    new Keyframe(0.29167F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)))
            .build();
}

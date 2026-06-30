package com.core.ads.domain.policy

interface AdsPolicyEvaluator {

    fun evaluate(context: AdsPolicyContext): AdsPolicyDecision
}
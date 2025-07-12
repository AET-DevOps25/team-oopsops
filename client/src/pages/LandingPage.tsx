import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { UploadCloud, ShieldCheck, FileText, ArrowRight, Sparkles } from "lucide-react";

const LandingPage: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-br from-slate-50 to-blue-50">
      <main className="flex-1 px-6 py-12">

        <section className="text-center max-w-4xl mx-auto mb-16">
          <div className="mb-4">
            <Badge variant="secondary" className="bg-blue-100 text-blue-800 px-4 py-2 text-sm font-medium hover:bg-blue-200 transition-colors">
              <Sparkles className="w-4 h-4 mr-2" />
              AI-Powered Document Processing
            </Badge>
          </div>
          <h1 className="text-6xl font-bold mb-4 bg-gradient-to-r from-gray-900 to-blue-600 bg-clip-text text-transparent">
            Welcome to Redacta
          </h1>
          <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto leading-relaxed">
            Transform your sensitive documents with intelligent anonymization and smart summaries.
            <span className="font-semibold text-gray-700"> Privacy-first, AI-powered, lightning-fast.</span>
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/register">
              <Button size="lg" className="text-lg px-8 py-6 shadow-lg hover:shadow-xl transition-shadow duration-200 bg-blue-600 hover:bg-blue-700">
                Get Started
                <ArrowRight className="w-5 h-5 ml-2" />
              </Button>
            </Link>
            <Link to="/login">
              <Button variant="outline" size="lg" className="text-lg px-8 py-6 border-2 hover:bg-gray-50 transition-colors">
                Sign In
              </Button>
            </Link>
          </div>
        </section>

        <section className="max-w-6xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="hover:shadow-lg transition-shadow duration-200 border-0 shadow-md">
              <CardHeader className="flex flex-col items-center text-center pb-3">
                <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center mb-3">
                  <UploadCloud className="w-8 h-8 text-white" />
                </div>
                <CardTitle className="text-xl font-semibold">Easy Upload</CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-muted-foreground leading-relaxed">
                  Simply drag & drop your PDF or browse to upload.
                  <span className="font-medium text-gray-700"> Up to 20MB, secure and instant.</span>
                </p>
              </CardContent>
            </Card>
            <Card className="hover:shadow-lg transition-shadow duration-200 border-0 shadow-md">
              <CardHeader className="flex flex-col items-center text-center pb-3">
                <div className="w-16 h-16 bg-gradient-to-br from-green-500 to-green-600 rounded-2xl flex items-center justify-center mb-3">
                  <ShieldCheck className="w-8 h-8 text-white" />
                </div>
                <CardTitle className="text-xl font-semibold">Smart Anonymization</CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-muted-foreground leading-relaxed">
                  AI automatically detects and redacts sensitive information.
                  <span className="font-medium text-gray-700"> GDPR compliant and enterprise-ready.</span>
                </p>
              </CardContent>
            </Card>
            <Card className="hover:shadow-lg transition-shadow duration-200 border-0 shadow-md">
              <CardHeader className="flex flex-col items-center text-center pb-3">
                <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-purple-600 rounded-2xl flex items-center justify-center mb-3">
                  <FileText className="w-8 h-8 text-white" />
                </div>
                <CardTitle className="text-xl font-semibold">Quick Summaries</CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-muted-foreground leading-relaxed">
                  Generate intelligent summaries that capture key insights.
                  <span className="font-medium text-gray-700"> Save hours of reading time.</span>
                </p>
              </CardContent>
            </Card>
          </div>
        </section>
      </main>
    </div>
  );
};

export default LandingPage;
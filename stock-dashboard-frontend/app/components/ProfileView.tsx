"use client";

import { useState } from 'react';
import { User } from '@/model/generated/v1/user_dto';

interface ProfileViewProps {
  user: User;
}

export default function ProfileView({ user }: ProfileViewProps) {
  const [activeTab, setActiveTab] = useState<'basic' | 'preferences'>('preferences');
  const [fullName, setFullName] = useState(user.fullName);
  const [email, setEmail] = useState(user.email);

  return (
    <div className="min-h-screen bg-background text-on-surface pt-navbar">
      <div className="container mx-auto p-4 max-w-4xl">
        {/* Profile Header Section */}
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
          <div>
            <h1 className="text-5xl font-bold mb-2 tracking-tight">Account Profile</h1>
            <p className="text-lg text-on-surface-variant max-w-lg">
              Manage your personal information, notification settings, and display preferences for your institutional trading terminal.
            </p>
          </div>
          <div className="flex gap-4">
            <button className="border border-outline-variant px-6 py-2 rounded font-mono text-sm hover:bg-surface-container-high transition-colors">
              DISCARD
            </button>
            <button className="bg-primary text-on-primary px-6 py-2 rounded font-mono text-sm font-bold hover:opacity-90 transition-opacity">
              SAVE CHANGES
            </button>
          </div>
        </div>

        {/* Tab System */}
        <div className="mb-12">
          <div className="flex gap-10 border-b border-surface-container-highest">
            <button
              className={`pb-4 font-mono text-sm tracking-widest border-b-2 transition-all ${
                activeTab === 'basic'
                  ? 'text-primary border-primary'
                  : 'text-on-surface-variant border-transparent hover:text-primary'
              }`}
              onClick={() => setActiveTab('basic')}
            >
              BASIC INFORMATION
            </button>
            <button
              className={`pb-4 font-mono text-sm tracking-widest border-b-2 transition-all ${
                activeTab === 'preferences'
                  ? 'text-primary border-primary'
                  : 'text-on-surface-variant border-transparent hover:text-primary'
              }`}
              onClick={() => setActiveTab('preferences')}
            >
              PREFERENCES
            </button>
          </div>
        </div>

        {/* Content Canvas */}
        {activeTab === 'basic' && (
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="flex flex-col gap-8 md:col-span-12">
              <div className="space-y-6">
                <div className="flex flex-col gap-2">
                  <label className="font-mono text-sm text-on-surface-variant uppercase">Full Name</label>
                  <input
                    className="bg-transparent border border-surface-container-highest px-4 py-3 font-mono text-sm focus:border-primary focus:outline-none transition-colors"
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                  />
                </div>
                <div className="flex flex-col gap-2">
                  <label className="font-mono text-sm text-on-surface-variant uppercase">Email Address</label>
                  <input
                    className="bg-transparent border border-surface-container-highest px-4 py-3 font-mono text-sm focus:border-primary focus:outline-none transition-colors"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div className="flex flex-col gap-2">
                  <label className="font-mono text-sm text-on-surface-variant uppercase">Account Number</label>
                  <div className="bg-surface-container border border-surface-container-highest px-4 py-3 font-mono text-sm text-on-surface-variant cursor-not-allowed">
                    {user.id}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'preferences' && (
          <div className="flex flex-col gap-12">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Regional Settings */}
              <div className="p-8 bg-surface-container border border-surface-container-highest rounded space-y-6">
                <div className="flex items-center gap-3 mb-4">
                  <span className="material-symbols-outlined text-primary">language</span>
                  <h3 className="text-xl font-medium">Regional</h3>
                </div>
                <div className="flex flex-col gap-4">
                  <div className="flex flex-col gap-2">
                    <label className="font-mono text-sm text-on-surface-variant uppercase">Default Currency</label>
                    <select defaultValue="INR" className="bg-transparent border border-surface-container-highest px-3 py-2 font-mono text-sm focus:border-primary focus:outline-none">
                      <option value="INR">INR - Indian Rupee</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
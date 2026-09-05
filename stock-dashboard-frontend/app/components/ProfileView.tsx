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
    <div className="min-h-screen bg-background pt-navbar text-on-surface">
      <main className="mx-auto max-w-6xl px-6 py-12 md:px-16">
        <header className="mb-12 flex flex-col justify-between gap-6 border-b border-outline-variant pb-8 md:flex-row md:items-end">
          <div>
            <h1 className="mb-2 text-5xl font-bold tracking-tight">Account Profile</h1>
            <p className="text-lg text-on-surface-variant max-w-lg">
              Manage your personal information, notification settings, and display preferences for your institutional trading terminal.
            </p>
          </div>
          <div className="flex gap-4">
            <button className="border border-outline-variant bg-white px-6 py-2 font-mono text-sm text-on-surface transition-colors hover:bg-surface-container-high">
              DISCARD
            </button>
            <button className="bg-primary px-6 py-2 font-mono text-sm font-bold text-on-primary transition-opacity hover:opacity-90">
              SAVE CHANGES
            </button>
          </div>
        </header>

        <div className="mb-12">
          <div className="flex gap-10 border-b border-outline-variant">
            <button
              className={`border-b-2 pb-4 font-mono text-sm tracking-widest transition-all ${
                activeTab === 'basic'
                  ? 'text-primary border-primary'
                  : 'text-on-surface-variant border-transparent hover:text-primary'
              }`}
              onClick={() => setActiveTab('basic')}
            >
              BASIC INFORMATION
            </button>
            <button
              className={`border-b-2 pb-4 font-mono text-sm tracking-widest transition-all ${
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

        {activeTab === 'basic' && (
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <div className="space-y-6">
              <div className="flex flex-col gap-2">
                <label className="font-mono text-sm uppercase text-on-surface-variant">Full Name</label>
                <input
                  className="border border-outline-variant bg-transparent px-4 py-3 font-mono text-sm transition-colors focus:border-primary focus:outline-none"
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />
              </div>
              <div className="flex flex-col gap-2">
                <label className="font-mono text-sm uppercase text-on-surface-variant">Email Address</label>
                <input
                  className="border border-outline-variant bg-transparent px-4 py-3 font-mono text-sm transition-colors focus:border-primary focus:outline-none"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="flex flex-col gap-2">
                <label className="font-mono text-sm uppercase text-on-surface-variant">Account Number</label>
                <div className="border border-outline-variant bg-surface-container px-4 py-3 font-mono text-sm text-on-surface-variant">
                  {user.id}
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'preferences' && (
          <div className="border border-outline-variant bg-white p-6 shadow-sm">
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div className="space-y-6 border border-outline-variant bg-background p-8">
                <div className="flex items-center gap-3 mb-4">
                  <span className="material-symbols-outlined text-primary">language</span>
                  <h3 className="text-xl font-medium">Regional</h3>
                </div>
                <div className="flex flex-col gap-4">
                  <div className="flex flex-col gap-2">
                    <label className="font-mono text-sm uppercase text-on-surface-variant">Default Currency</label>
                    <div className="border border-outline-variant bg-white px-3 py-2 font-mono text-sm text-on-surface">
                      INR - Indian Rupee
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
